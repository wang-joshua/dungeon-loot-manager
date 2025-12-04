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
// Note: This function is overridden below with sorting functionality
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
                // Show Use button only for potions
                if (item && item.type === "POTION") {
                    const useBtn = document.createElement("button");
                    useBtn.textContent = "Use";
                    useBtn.onclick = () => useItem(inv.itemId);
                    buttonRow.appendChild(useBtn);
                }

                const sellBtn = document.createElement("button");
                sellBtn.textContent = "Sell";
                sellBtn.onclick = () => sellItem(inv.itemId);
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

//== Extra Credit: Enhanced Features ==\\

// Combat Log
let combatLog = [];

function addCombatLogEntry(message, type = 'info') {
    combatLog.push({ message, type, timestamp: Date.now() });
    if (combatLog.length > 20) {
        combatLog.shift();
    }
    updateCombatLog();
}

function updateCombatLog() {
    const logContainer = document.getElementById("combat-log");
    if (!logContainer) return;
    
    if (combatLog.length === 0) {
        logContainer.style.display = 'none';
        return;
    }
    
    logContainer.style.display = 'block';
    logContainer.innerHTML = combatLog.slice(-10).reverse().map(entry => {
        const time = new Date(entry.timestamp).toLocaleTimeString();
        return `<div class="combat-log-entry">[${time}] ${entry.message}</div>`;
    }).join('');
    logContainer.scrollTop = logContainer.scrollHeight;
}

// Statistics
let gameStats = {
    totalGoldEarned: 0,
    totalGoldSpent: 0,
    enemiesDefeated: 0,
    itemsLooted: 0,
    itemsSold: 0,
    potionsUsed: 0,
    battlesWon: 0,
    battlesLost: 0
};

function updateStatistics() {
    const statsContent = document.getElementById("stats-content");
    if (!statsContent) return;
    
    statsContent.innerHTML = `
        <div class="stat-item">
            <div class="stat-value">${gameStats.totalGoldEarned}</div>
            <div class="stat-label">Gold Earned</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.totalGoldSpent}</div>
            <div class="stat-label">Gold Spent</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.enemiesDefeated}</div>
            <div class="stat-label">Enemies Defeated</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.itemsLooted}</div>
            <div class="stat-label">Items Looted</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.itemsSold}</div>
            <div class="stat-label">Items Sold</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.potionsUsed}</div>
            <div class="stat-label">Potions Used</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.battlesWon}</div>
            <div class="stat-label">Battles Won</div>
        </div>
        <div class="stat-item">
            <div class="stat-value">${gameStats.battlesLost}</div>
            <div class="stat-label">Battles Lost</div>
        </div>
    `;
}

function showStatistics() {
    const statsCard = document.getElementById("stats-card");
    if (statsCard) {
        statsCard.style.display = statsCard.style.display === 'none' ? 'block' : 'none';
        if (statsCard.style.display === 'block') {
            updateStatistics();
        }
    }
}

// Health Bar
function updateHealthBar(player) {
    const container = document.getElementById("health-bar-container");
    const bar = document.getElementById("health-bar");
    if (!container || !bar) return;
    
    if (!player || player.maxHealth === 0) {
        container.style.display = 'none';
        return;
    }
    
    container.style.display = 'block';
    const percentage = (player.currentHealth / player.maxHealth) * 100;
    bar.style.width = `${Math.max(0, Math.min(100, percentage))}%`;
    
    // Color based on health percentage
    if (percentage > 60) {
        bar.style.background = 'linear-gradient(90deg, #22c55e 0%, #16a34a 100%)';
    } else if (percentage > 30) {
        bar.style.background = 'linear-gradient(90deg, #f97316 0%, #ea580c 100%)';
    } else {
        bar.style.background = 'linear-gradient(90deg, #ef4444 0%, #dc2626 100%)';
    }
}

// Item Sorting
let currentSort = 'name';
let sortAscending = true;

function sortInventory(sortBy) {
    if (currentSort === sortBy) {
        sortAscending = !sortAscending;
    } else {
        currentSort = sortBy;
        sortAscending = true;
    }
    
    // Update active button
    document.querySelectorAll('.sort-btn').forEach(btn => {
        btn.classList.remove('active');
        if (btn.dataset.sort === sortBy) {
            btn.classList.add('active');
        }
    });
    
    loadInventory();
}

// Enhanced loadInventory with sorting - replaces the original function
loadInventory = async function() {
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
        
        // Sort inventory
        let sortedInventory = [...inventory];
        sortedInventory.sort((a, b) => {
            const itemA = byId[a.itemId];
            const itemB = byId[b.itemId];
            if (!itemA || !itemB) return 0;
            
            let comparison = 0;
            switch (currentSort) {
                case 'name':
                    comparison = (itemA.name || '').localeCompare(itemB.name || '');
                    break;
                case 'type':
                    comparison = (itemA.type || '').localeCompare(itemB.type || '');
                    break;
                case 'rarity':
                    const rarityOrder = { 'COMMON': 1, 'RARE': 2, 'EPIC': 3, 'LEGENDARY': 4 };
                    comparison = (rarityOrder[itemA.rarity] || 0) - (rarityOrder[itemB.rarity] || 0);
                    break;
                case 'value':
                    comparison = (itemA.goldValue || 0) - (itemB.goldValue || 0);
                    break;
            }
            return sortAscending ? comparison : -comparison;
        });
        
        const weaponId = currentPlayer?.equippedWeaponItemId;
        const armorId = currentPlayer?.equippedArmorItemId;
        const weaponDur = currentPlayer?.equippedWeaponDurability;
        const armorDur = currentPlayer?.equippedArmorDurability;
        
        sortedInventory.forEach(inv => {
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
                    Value: ${item ? item.goldValue : "?"}g |
                    Rarity: ${item ? item.rarity : "?"}
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
                // Show Use button only for potions
                if (item && item.type === "POTION") {
                    const useBtn = document.createElement("button");
                    useBtn.textContent = "Use";
                    useBtn.onclick = () => useItem(inv.itemId);
                    buttonRow.appendChild(useBtn);
                }
                
                const sellBtn = document.createElement("button");
                sellBtn.textContent = "Sell";
                sellBtn.onclick = () => sellItem(inv.itemId);
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
};

// Enhanced functions with statistics and combat log
const originalSellItem = sellItem;
const sellItemWithStats = async function(itemId) {
    try {
        // Get item info before selling for stats
        const items = await fetchJson(`${API_BASE}/items`);
        const item = items.find(i => i.id === itemId);
        const itemValue = item ? item.goldValue : 0;
        const itemName = item ? item.name : "item";
        
        // Call the sell endpoint
        const result = await fetchJson(`${API_BASE}/inventory/sell/${itemId}`, { method: "POST" });
        
        // Update statistics
        gameStats.itemsSold++;
        gameStats.totalGoldEarned += itemValue;
        addCombatLogEntry(`Sold ${itemName} for ${itemValue}g`, 'success');
        
        // Refresh UI
        await Promise.all([loadPlayer(), loadInventory()]);
    } catch (err) {
        console.error("Sell error:", err);
        alert("Failed to sell item: " + (err.message || "Unknown error"));
    }
};
sellItem = sellItemWithStats;

const originalUseItem = useItem;
useItem = async function(itemId) {
    try {
        const result = await fetchJson(`${API_BASE}/inventory/use/${itemId}`, { method: "POST" });
        gameStats.potionsUsed++;
        const items = await fetchJson(`${API_BASE}/items`);
        const item = items.find(i => i.id === itemId);
        if (item) {
            addCombatLogEntry(`Used ${item.name} to restore health`, 'heal');
        }
        await Promise.all([
            loadPlayer(),
            loadInventory(),
            loadBattleState()
        ]);
    } catch (err) {
        console.error("Use item error:", err);
        alert("Failed to use item: " + (err.message || "Only potions can be used!"));
    }
};

const originalStartEncounter = startEncounter;
startEncounter = async function() {
    try {
        const state = await fetchJson(`${API_BASE}/battle/start`, { method: "POST" });
        if (state.monster) {
            addCombatLogEntry(`Encounter started: ${state.monster.kind} appeared!`, 'battle');
        }
        renderBattleState(state);
    } catch (err) {
        console.error(err);
        alert("Failed to start encounter.");
    }
};

const originalAttack = attack;
attack = async function() {
    try {
        const battleInfo = document.getElementById("battle-info");
        if (battleInfo) {
            battleInfo.classList.add("attack-animation");
            setTimeout(() => battleInfo.classList.remove("attack-animation"), 300);
        }
        
        const state = await fetchJson(`${API_BASE}/battle/attack`, { method: "POST" });
        
        if (state.status === "MONSTER_DEFEATED") {
            gameStats.enemiesDefeated++;
            gameStats.battlesWon++;
            addCombatLogEntry(`Victory! Defeated ${state.message.split(' ').pop()}`, 'victory');
        } else if (state.status === "PLAYER_DEFEATED") {
            gameStats.battlesLost++;
            addCombatLogEntry(`Defeated! ${state.message}`, 'defeat');
        } else if (state.status === "GAME_OVER") {
            gameStats.battlesLost++;
            addCombatLogEntry(`Game Over! ${state.message}`, 'defeat');
        } else {
            addCombatLogEntry(state.message, 'battle');
        }
        
        renderBattleState(state);
    } catch (err) {
        console.error(err);
        alert("Failed to attack.");
    }
};

const originalRenderBattleState = renderBattleState;
renderBattleState = function(state) {
    originalRenderBattleState(state);
    updateHealthBar(state.player);
    
    if (state.player) {
        const battleInfo = document.getElementById("battle-info");
        if (battleInfo && state.monster) {
            battleInfo.classList.add("damage-flash");
            setTimeout(() => battleInfo.classList.remove("damage-flash"), 500);
        }
    }
};

const originalLoadPlayer = loadPlayer;
loadPlayer = async function() {
    await originalLoadPlayer();
    if (currentPlayer) {
        updateHealthBar(currentPlayer);
    }
};

// Keyboard Shortcuts
document.addEventListener("keydown", (e) => {
    if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA') {
        return;
    }
    
    switch (e.key.toLowerCase()) {
        case 'a':
            e.preventDefault();
            const attackBtn = document.getElementById("attack-btn");
            if (attackBtn && !attackBtn.disabled) {
                attackBtn.click();
            }
            break;
        case 's':
            e.preventDefault();
            const startBtn = document.getElementById("start-encounter-btn");
            if (startBtn && !startBtn.disabled) {
                startBtn.click();
            }
            break;
        case 'r':
            if (e.ctrlKey || e.metaKey) {
                e.preventDefault();
                return;
            }
            e.preventDefault();
            if (confirm("Are you sure you want to restart the game? All progress will be lost.")) {
                const restartBtn = document.getElementById("restart-btn");
                if (restartBtn) {
                    restartBtn.click();
                }
            }
            break;
        case 'i':
            e.preventDefault();
            const inventoryCard = document.getElementById("inventory-card");
            if (inventoryCard) {
                inventoryCard.scrollIntoView({ behavior: 'smooth' });
            }
            break;
    }
});

// Confirmation dialogs for destructive actions
const originalRestartGame = restartGame;
restartGame = async function() {
    if (!confirm("Are you sure you want to restart the game? All progress will be lost.")) {
        return;
    }
    
    // Reset statistics
    gameStats = {
        totalGoldEarned: 0,
        totalGoldSpent: 0,
        enemiesDefeated: 0,
        itemsLooted: 0,
        itemsSold: 0,
        potionsUsed: 0,
        battlesWon: 0,
        battlesLost: 0
    };
    combatLog = [];
    
    await originalRestartGame();
};

// Enhanced sell with confirmation for valuable items
const sellItemWithConfirmation = sellItem;
sellItem = async function(itemId) {
    try {
        const items = await fetchJson(`${API_BASE}/items`);
        const item = items.find(i => i.id === itemId);
        
        if (item && (item.rarity === 'EPIC' || item.rarity === 'LEGENDARY')) {
            if (!confirm(`Are you sure you want to sell ${item.name} (${item.rarity}) for ${item.goldValue}g?`)) {
                return;
            }
        }
        
        await sellItemWithConfirmation(itemId);
    } catch (err) {
        console.error(err);
        alert("Failed to check item details: " + (err.message || "Unknown error"));
    }
};

window.addEventListener("DOMContentLoaded", async () => {
    showInstructions();
    setupEvents();
    
    // Setup sort buttons
    document.querySelectorAll('.sort-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            sortInventory(btn.dataset.sort);
        });
    });
    
    // Setup statistics button
    const showStatsBtn = document.getElementById("show-stats-btn");
    if (showStatsBtn) {
        showStatsBtn.addEventListener('click', showStatistics);
    }
    
    await Promise.all([
        loadAllItems(),
        loadPlayer(),
        loadInventory(),
        loadBattleState()
    ]);
});
