const API_BASE = "http://localhost:8080/api";

let currentPlayer = null;
let allItems = [];
let storeItems = [];


async function fetchJson(url, options = {}) {
    const res = await fetch(url, options);
    if (!res.ok) {
        const text = await res.text();
        throw new Error(`Request failed: ${res.status} ${res.statusText} - ${text}`);
    }
    return res.json();
}

//== Player ==\\
async function loadPlayer() {
    const container = document.getElementById("player-info");
    try {
        const player = await fetchJson(`${API_BASE}/player`);
        currentPlayer = player;

        const nameInput = document.getElementById("player-name-input");
        if (nameInput && !nameInput.value) {
            nameInput.value = player.name || "";
        }

        let weaponPower = 0;
        let armorPower = 0;

        if (allItems.length > 0) {
            if (player.equippedWeaponItemId) {
                const w = allItems.find(i => i.id === player.equippedWeaponItemId);
                if (w) {
                    weaponPower = w.power;
                }
            }
            if (player.equippedArmorItemId) {
                const a = allItems.find(i => i.id === player.equippedArmorItemId);
                if (a) {
                    armorPower = a.power;
                }
            }
        }

        const effectiveAttack = player.baseAttack + weaponPower;
        const baseMaxHealth = Math.max(1, player.maxHealth - armorPower);
        container.innerHTML = `
            <p><strong>Name:</strong> ${player.name}</p>
            <p><strong>Level:</strong> ${player.level}</p>
            <p><strong>XP:</strong> ${player.experience}</p>
            <p><strong>Attack:</strong> ${player.baseAttack} + ${weaponPower} = ${effectiveAttack}</p>
            <p><strong>Health:</strong> ${player.currentHealth} / ${player.maxHealth}
                <span class="small-muted">(base ${baseMaxHealth} + armor ${armorPower})</span>
            </p>
            <p><strong>Gold:</strong> ${player.gold}</p>
            <p><small>Encounters: ${player.encountersFoughtCount}</small></p>
            <p><small>Revives left: ${player.revivesLeft}</small></p>
        `;

        const debtStatusEl = document.getElementById("debt-status");
        if (debtStatusEl) {
            if (player.debtPaid) {
                debtStatusEl.textContent = "Debt paid! You have completed your Hero Academy obligations.";
            } else {
                debtStatusEl.textContent = "Debt unpaid. Save up 300g to win!";
            }
        }
    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error">Failed to load player.</p>`;
    }
}

//== Store ==\\
async function loadStore() {
    const container = document.getElementById("store-items");
    if (!container) return;
    container.innerHTML = "";

    try {
        const items = await fetchJson(`${API_BASE}/shop`);
        storeItems = items;

        if (!items.length) {
            container.innerHTML = "<p>No items in stock.</p>";
            return;
        }

        items.forEach(item => {
            const div = document.createElement("div");
            div.className = `list-item rarity-${item.rarity || "COMMON"}`;

            const main = document.createElement("div");
            main.className = "item-main";
            main.innerHTML = `
                <span class="item-title">${item.name}</span>
                <span class="item-meta">
                    Type: ${item.type} |
                    Rarity: ${item.rarity} |
                    Power: ${item.power} |
                    Price: ${item.goldValue}g
                </span>
            `;

            const buyBtn = document.createElement("button");
            buyBtn.textContent = "Buy";
            buyBtn.onclick = () => buyItem(item.id);

            div.appendChild(main);
            div.appendChild(buyBtn);
            container.appendChild(div);
        });
    } catch (err) {
        console.error(err);
        container.innerHTML = `<p class="error">Failed to load store.</p>`;
    }
}

async function buyItem(itemId) {
    try {
        await fetchJson(`${API_BASE}/shop/buy/${itemId}`, { method: "POST" });
        await Promise.all([
            loadPlayer(),
            loadInventory(),
            loadStore()
        ]);
    } catch (err) {
        console.error(err);
        alert("Could not buy item (maybe not enough gold).");
    }
}

async function loadAllItems() {
    try {
        const items = await fetchJson(`${API_BASE}/items`);
        allItems = items;
    } catch (err) {
        console.error("Failed to load item catalog for stats.", err);
    }
}

//== Inventory ==\\
async function loadInventory() {
    const list = document.getElementById("inventory-list");

    try {
        const inventory = await fetchJson(`${API_BASE}/inventory`);
        if (!inventory.length) {
            list.innerHTML = "<p>Inventory is empty.</p>";
            return;
        }
        list.innerHTML = "";

        const items = await fetchJson(`${API_BASE}/items`);
        const byId = {};
        items.forEach(i => byId[i.id] = i);

        const weaponId = currentPlayer?.equippedWeaponItemId;
        const armorId = currentPlayer?.equippedArmorItemId;
        const weaponDur = currentPlayer?.equippedWeaponDurability;
        const armorDur = currentPlayer?.equippedArmorDurability;

        list.innerHTML = "";
        inventory.forEach(inv => {
            const item = byId[inv.itemId];
            const div = document.createElement("div");
            div.className = "list-item";

            const left = document.createElement("div");
            left.className = "item-main";

            const isWeaponEquipped = item && weaponId === item.id;
            const isArmorEquipped = item && armorId === item.id;

            let extra = "";
            if (isWeaponEquipped) {
                extra = ` [Equipped WEAPON, Durability: ${weaponDur}]`;
            } else if (isArmorEquipped) {
                extra = ` [Equipped ARMOR, Durability: ${armorDur}]`;
            }

            left.innerHTML = `
                <span class="item-title">${item ? item.name : "Unknown"} (x${inv.quantity}) ${extra}</span>
                <span class="item-meta">
                    Type: ${item ? item.type : "?"} |
                    Power: ${item ? item.power : "?"} |
                    Value: ${item ? item.goldValue : "?"}g
                </span>
            `;

            const buttonRow = document.createElement("div");
            buttonRow.style.display = "flex";
            buttonRow.style.gap = "0.25rem";

            if (isWeaponEquipped || isArmorEquipped) {
                const unequipBtn = document.createElement("button");
                unequipBtn.textContent = "Unequip";
                unequipBtn.onclick = () => unequipItem(inv.itemId);
                buttonRow.appendChild(unequipBtn);
            } else {
                // Not equipped: normal buttons
                const useBtn = document.createElement("button");
                useBtn.textContent = "Use";
                useBtn.onclick = () => useItem(inv.itemId);

                const sellBtn = document.createElement("button");
                sellBtn.textContent = "Sell";
                sellBtn.onclick = () => sellItem(inv.itemId);

                buttonRow.appendChild(useBtn);
                buttonRow.appendChild(sellBtn);

                if (item && (item.type === "WEAPON" || item.type === "ARMOR")) {
                    const equipBtn = document.createElement("button");
                    equipBtn.textContent = "Equip";
                    equipBtn.onclick = () => equipItem(inv.itemId);
                    buttonRow.appendChild(equipBtn);
                }
            }

            div.appendChild(left);
            div.appendChild(buttonRow);
            list.appendChild(div);
        });
    } catch (err) {
        console.error(err);
        list.innerHTML = `<p class="error">Failed to load inventory.</p>`;
    }
}

async function sellItem(itemId) {
    try {
        await fetchJson(`${API_BASE}/inventory/sell/${itemId}`, { method: "POST" });
        await Promise.all([loadPlayer(), loadInventory()]);
    } catch (err) {
        console.error(err);
        alert("Failed to sell item.");
    }
}

async function useItem(itemId) {
    try {
        await fetchJson(`${API_BASE}/inventory/use/${itemId}`, { method: "POST" });
        await Promise.all([
            loadPlayer(),
            loadInventory(),
            loadBattleState()
        ]);
    } catch (err) {
        console.error(err);
        alert("Only potions can be used!");
    }
}

async function setPlayerName() {
    const input = document.getElementById("player-name-input");
    if (!input) return;
    const name = input.value.trim();
    if (!name) {
        alert("Please enter the name of your Hero.");
        return;
    }
    try {
        await fetchJson(`${API_BASE}/player/name`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name })
        });
        await loadPlayer();
    } catch (err) {
        console.error(err);
        alert("Failed to set name.");
    }
}

async function equipItem(itemId) {
    try {
        await fetchJson(`${API_BASE}/inventory/equip/${itemId}`, { method: "POST" });
        await Promise.all([
            loadPlayer(),
            loadInventory(),
            loadBattleState()
        ]);
    } catch (err) {
        console.error(err);
        alert("Failed to equip item.");
    }
}

async function unequipItem(itemId) {
    try {
        await fetchJson(`${API_BASE}/inventory/unequip/${itemId}`, { method: "POST" });
        await Promise.all([
            loadPlayer(),
            loadInventory(),
            loadBattleState()
        ]);
    } catch (err) {
        console.error(err);
        alert("Failed to unequip item.");
    }
}

async function payDebt() {
    try {
        const player = await fetchJson(`${API_BASE}/player/pay-debt`, {
            method: "POST"
        });
        currentPlayer = player;
        await loadPlayer();
        alert("Congrats, your debt has been cleared, you win! To restart click the Restart Game button.");
    } catch (err) {
        console.error(err);
        alert("You don't have enough gold (need 300)!");
    }
}

async function restartGame() {
    try {
        const player = await fetchJson(`${API_BASE}/player/restart`, {
            method: "POST"
        });
        currentPlayer = player;
        await Promise.all([
            loadAllItems(),
            loadPlayer(),
            loadInventory(),
            loadBattleState()
        ]);
    } catch (err) {
        console.error(err);
        alert("Failed to restart game.");
    }
}

//== Battle ==\\
async function loadBattleState() {
    const info = document.getElementById("battle-info");
    try {
        const state = await fetchJson(`${API_BASE}/battle`);
        renderBattleState(state);
    } catch (err) {
        console.error(err);
        info.innerHTML = `<p class="error">Failed to load battle state.</p>`;
    }
}

function renderBattleState(state) {
    const info = document.getElementById("battle-info");
    const player = state.player;
    const monster = state.monster;

    currentPlayer = player;

    let html = `<p>${state.message}</p>`;

    html += `<div style="margin-top:0.5rem">
        <p><strong>Player HP:</strong> ${player.currentHealth} / ${player.maxHealth}</p>
        <p><strong>Revives Left:</strong> ${player.revivesLeft}</p>
    </div>`;

    if (monster) {
        html += `<div style="margin-top:0.5rem">
            <p><strong>Enemy:</strong> ${monster.kind}</p>
            <p><strong>Enemy HP:</strong> ${monster.currentHealth} / ${monster.maxHealth}</p>
            <p><strong>Enemy Attack:</strong> ${monster.attackPower}</p>
        </div>`;
    }

    info.innerHTML = html;

    const attackBtn = document.getElementById("attack-btn");
    const startBtn = document.getElementById("start-encounter-btn");

    if (player.gameOver || player.debtPaid) {
        attackBtn.disabled = true;
        startBtn.disabled = true;
    } else {
        attackBtn.disabled = false;
        startBtn.disabled = false;
    }

    // refresh
    loadPlayer();
    loadInventory();
    loadStore();
}

async function startEncounter() {
    try {
        const state = await fetchJson(`${API_BASE}/battle/start`, { method: "POST" });
        renderBattleState(state);
    } catch (err) {
        console.error(err);
        alert("Failed to start encounter.");
    }
}

async function attack() {
    try {
        const state = await fetchJson(`${API_BASE}/battle/attack`, { method: "POST" });
        renderBattleState(state);
    } catch (err) {
        console.error(err);
        alert("Failed to attack.");
    }
}

//== Wiring ==\\
function setupEvents() {
    const refreshBtn = document.getElementById("refresh-player-btn");
    if (refreshBtn) {
        refreshBtn.addEventListener("click", loadPlayer);
    }

    const filterBtn = document.getElementById("apply-filter-btn");
    if (filterBtn) {
        filterBtn.addEventListener("click", loadItems);
    }

    const startBtn = document.getElementById("start-encounter-btn");
    if (startBtn) {
        startBtn.addEventListener("click", startEncounter);
    }

    const attackBtn = document.getElementById("attack-btn");
    if (attackBtn) {
        attackBtn.addEventListener("click", attack);
    }

    const setNameBtn = document.getElementById("set-name-btn");
    if (setNameBtn) {
        setNameBtn.addEventListener("click", setPlayerName);
    }

    const payDebtBtn = document.getElementById("pay-debt-btn");
    if (payDebtBtn) {
        payDebtBtn.addEventListener("click", payDebt);
    }

    const restartBtn = document.getElementById("restart-btn");
    if (restartBtn) {
        restartBtn.addEventListener("click", restartGame);
    }
}

function showInstructions() {
    alert(
        "Welcome to your Dungeon Loot Manager!\n\n" +
        "- Name your hero, then start encounters in the Battle card.\n" +
        "- Each encounter pits you against skeletons, orcs, bandits, or a dragon every 5th fight.\n" +
        "- Defeat enemies to earn gold and random loot.\n" +
        "- Equip WEAPONS to increase attack, and ARMOR to increase max health.\n" +
        "- Potions heal you; Valuables are just for selling.\n" +
        "- Visit the Store to buy better gear. The store refreshes every 5 battles.\n" +
        "- You have 2 revives; if you die a third time, it's game over.\n" +
        "- Pay 300 gold to clear your Hero Academy Debt and win!"
    );
}

window.addEventListener("DOMContentLoaded", async () => {
    showInstructions();
    setupEvents();
    await Promise.all([
        loadAllItems(),
        loadPlayer(),
        loadInventory(),
        loadBattleState()
    ]);
});
