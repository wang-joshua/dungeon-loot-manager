# Homework 09 – Dungeon Exploration!

**Authors:** Kellen, Xavier  
**Topics:** Spring Boot, JPA, REST APIs

---

## Problem Description

The CS1331 TA team is in trouble! Their favorite game, *Dungeon Market Exploration*, is broken! It is up to you to get it up and running again so the TAs can get back to neglecting their schoolwork.

---

## Solution Description

This assignment's format will diverge from homeworks 1-8. We will be providing you a **semi-complete Spring Boot project**, and you will have to complete marked TODOs in specific Java files. Your submission will consist of the entire Spring Boot project with your modifications.

**Note:** The autograder for this assignment will check to see if your project builds. We will be manually grading each submission, so please keep that in mind.

---

## Notes

1. **Before you start implementing any requirements**, we encourage you to read throught the entirety of this file so you can get a grasp on the project structure and tasks.
2. **Be mindful of which files you modify.** If you change files that are not outlined in the instructions, it will be difficult to find and fix problems.
3. **Test your project locally** before submitting. Please refer to the "Testing Your Application" section for more details.
4. All variables should be **inaccessible from other classes** and must require an instance to be accessed through, unless specified otherwise.
5. When throwing exceptions, a **descriptive and specific message** should be provided.
6. **Not much code will be written** comparative to your previous homeworks. Most of the work for this assignment is understanding the project structure and context.

---

## Implementation Requirements

### Part 1: `/model` Package

Navigate to the `src/main/java/edu/gatech/cs1331/hw09/backend/model` folder. Here you will find Java files that represent the different schemas we will need for our game to run. You will add the correct annotations in each file so that our models behave correctly.

#### **Player.java**
This class represents how we store a player in our database.
- This class must be managed by the JPA and mapped to our database using the correct annotation.
- Each player should have a **unique ID** in our database.

#### **Item.java**
This class represents how we store an item in our database.
- This class must be managed by the JPA and mapped to our database using the correct annotation.
- Each item should have a **unique ID** in our database.

#### **InventoryItem.java**
This class represents how we store an inventory item in our database.
- This class must be managed by the JPA and mapped to our database using the correct annotation.
- Each inventory item should have a **unique ID** in our database.

---

### Part 2: `/controller` Package

Navigate to the `src/main/java/edu/gatech/cs1331/hw09/backend/controller` folder. Here is where we will define interactions with our database. You will need to implement the correct behavior for certain API routes.

---

#### **BattleController.java**

This class defines the behavior of a battle between a player and an enemy.

**Class Requirements:**
- This class must be managed by Spring and will define REST operations to our service.
  - **HINT:** This annotation will be a more specific `@Controller` type.
- The initial API endpoint should read as follows: `"/api/battle"`

**State Variable(s):**
```java
final BattleService battleService;
```

**Constructor:**
- A constructor that takes in the `BattleService` that this controller will call.

**Endpoints:**

1. **`GET /`**
   - This endpoint will take in nothing.
   - This endpoint will return the current state of the battle.
   - **HINT:** Look through the `BattleService.java` methods.

2. **`POST /start`**
   - This endpoint will take in nothing.
   - This endpoint will start the battle.
   - Returns a `BattleState` object.
   - **HINT:** Look through the `BattleService.java` methods.

3. **`POST /attack`**
   - This endpoint will take in nothing.
   - This endpoint will initiate an attack on an enemy using the current player.
   - Returns a `BattleState` object.
   - **HINT:** Look through the `BattleService.java` methods.

---

#### **InventoryController.java**

This class defines the behavior of the player's inventory management.

**Class Requirements:**
- This class must be managed by Spring and will define REST operations to our service.
  - **HINT:** This annotation will be a more specific `@Controller` type.
- The initial API endpoint should read as follows: `"/api/inventory"`

**State Variable(s):**
```java
final InventoryService inventoryService;
```

**Constructor:**
- A constructor that takes in `inventoryService` and sets it accordingly.

**Endpoints:**

1. **`GET /`**
   - This endpoint will take in nothing and returns a `List<InventoryItem>`.
   - This endpoint will retrieve the existing inventory.
   - **HINT:** Look through the `InventoryService.java` methods.

2. **`POST /loot/{itemId}`**
   - This endpoint will take in a `Long itemId` and will return an `InventoryItem` object.
   - This endpoint will loot an item based on the `itemId`.
   - **HINT:** Look through the `InventoryService.java` methods.

3. **`DELETE /drop/{itemId}`**
   - This endpoint will take in a `Long itemId` and will return an `InventoryItem` object.
   - This endpoint will delete an item based on the `itemId`.
   - **HINT:** Look through the `InventoryService.java` methods.

4. **`POST /equip/{itemId}`**
   - This endpoint will take in a `Long itemId` and will return an `InventoryItem` object.
   - This endpoint will equip the item based on the `itemId`.
   - **HINT:** Look through the `InventoryService.java` methods.

5. **`POST /unequip/{itemId}`**
   - This endpoint will take in a `Long itemId` and will return an `InventoryItem` object.
   - This endpoint will unequip the item based on the `itemId`.
   - **HINT:** Look through the `InventoryService.java` methods.

6. **`POST /sell/{itemId}`**
   - This endpoint will take in a `Long itemId` and will return an `InventoryItem` object.
   - This endpoint will sell the item based on the `itemId`.
   - **HINT:** Look through the `InventoryService.java` methods.

7. **`POST /use/{itemId}`**
   - This endpoint will take in a `Long itemId` and will return an `InventoryItem` object.
   - This endpoint will use the item based on the `itemId`.
   - **HINT:** Look through the `InventoryService.java` methods.

---

#### **PlayerController.java**

This class defines the behavior of player-related operations.

**Class Requirements:**
- This class must be managed by Spring and will define REST operations to our service.
  - **HINT:** This annotation will be a more specific `@Controller` type.
- The initial API endpoint should read as follows: `"/api/player"`

**State Variable(s):**
```java
final PlayerService playerService;
final InventoryService inventoryService;
```

**Constructor:**
- A constructor that takes in `playerService` and `inventoryService` and sets them accordingly.

**Endpoints:**

1. **`GET /`**
   - This endpoint will take in nothing and returns a `Player` object.
   - This endpoint will retrieve an existing player, or create a new one.
   - **HINT:** Look through the `PlayerService.java` methods.

2. **`PUT /`**
   - This endpoint will take in a `Player` object from the request body and will return a `Player` object.
   - This endpoint will update the player with the most recent state.
   - **HINT:** Look through the `PlayerService.java` methods.

3. **`POST /name`**
   - This method will take in a `RequestBody` (as a `Map<String, String>`) and returns a `Player` object.
   - Extract the `"name"` field from the request body.
   - If the name field in the request is blank or null, throw an `IllegalArgumentException` with an appropriate message.
   - This endpoint will set the name of the current player and return the saved player.
   - **HINT:** Look through the `PlayerService.java` methods.

4. **`POST /pay-debt`**
   - This endpoint will take in nothing.
   - This endpoint will pay the debt of the player.
   - **HINT:** Look through the `PlayerService.java` methods.

5. **`POST /restart`**
   - This endpoint will take in nothing.
   - This endpoint will clear the current inventory and restart the game.
   - **HINT:** Look through both `InventoryService.java` and `PlayerService.java` methods.

---

#### **ItemController.java**

This class defines the behavior of item-related operations.

**Class Requirements:**
- This class must be managed by Spring and will define REST operations to our service.
  - **HINT:** This annotation will be a more specific `@Controller` type.
- The initial API endpoint should read as follows: `"/api/items"`

**State Variable(s):**
```java
final ItemService itemService;
```

**Constructor:**
- A constructor that takes in `itemService` and sets it accordingly.

**Endpoints:**

1. **`GET /`**
   - This endpoint will take in nothing and returns a `List<Item>`.
   - This endpoint will retrieve all items in the game.
   - **HINT:** Look through the `ItemService.java` methods.

2. **`GET /{id}`**
   - This endpoint will take in a `Long id` and returns an `Item` object.
   - This endpoint will retrieve a specific item by its ID.
   - **HINT:** Look through the `ItemService.java` methods.

3. **`GET /rarity/{rarity}`**
   - This endpoint will take in a `String rarity` and returns a `List<Item>`.
   - This endpoint will retrieve all items that match the given rarity.
   - The rarity should be one of: "COMMON", "RARE", "EPIC", or "LEGENDARY".
   - **HINT:** Look through the `ItemService.java` methods.

---

#### **ShopController.java**

This class defines the behavior of the in-game shop.

**Class Requirements:**
- This class must be managed by Spring and will define REST operations to our service.
  - **HINT:** This annotation will be a more specific `@Controller` type.
- The initial API endpoint should read as follows: `"/api/shop"`

**State Variable(s):**
```java
final ShopService shopService;
```

**Constructor:**
- A constructor that takes in `shopService` and sets it accordingly.

**Endpoints:**

1. **`GET /`**
   - This endpoint will take in nothing and returns a `List<Item>`.
   - This endpoint will retrieve available shop items.
   - **HINT:** Look through the `ShopService.java` methods.

2. **`POST /buy/{itemId}`**
   - This endpoint will take in a `Long itemId` and returns an `Item` object.
   - This endpoint will purchase an item and add it to the player's inventory.
   - **HINT:** Look through the `ShopService.java` methods.


---

## Extra Credit [30 Points Maximum]

For this assignemnt, there is the opportunity to earn extra credit on top of the base rubic items. **All extra credit must be non-trivial** and demonstrate meaningful effort and understanding of web development concepts.

### 1. Graphics Enhancement [5-10 Points]

Enhance the visual appearance of the game's frontend interface.

**Examples:**
- Add custom CSS animations for battle sequences (attack animations, damage indicators)
- Create custom item icons or sprites for different item types
- Implement a dynamic health bar that visually updates in real-time
- Add particle effects for actions (sparkles when looting, explosions in battles)
- Design a custom game theme with cohesive color scheme and typography
- Add visual feedback for user actions (button hover effects, transition animations)

**Requirements:**
- Must modify files in the `src/main/resources/static` directory
- Changes must be visually noticeable and enhance user experience
- More complex enhancements earn more points (graded at TA discretion)
- Document your changes in a separate `EXTRA_CREDIT.md` file

---

### 2. Controls Enhancement [5-15 Points]

Improve or add new user interface controls and interactions.

**5-Point Examples:**
- Add keyboard shortcuts for common actions (e.g., press 'A' to attack, 'I' for inventory)
- Implement confirmation dialogs for destructive actions (selling valuable items, restarting game)
- Add tooltips that display item stats on hover

**10-15 Point Examples:**
- Create a drag-and-drop interface for inventory management
- Implement real-time search/filter for items in inventory or shop
- Add a settings panel where players can customize game preferences
- Create modal windows for detailed item inspection
- Implement undo/redo functionality for recent actions
- Implement an achievements/quest tracking system with progress indicators
- Add controller/gamepad support for navigation
- Implement auto-save functionality that persists game state

**Requirements:**
- Enhancement must significantly improve usability or add new interaction patterns
- Must maintain consistency with existing game mechanics
- More complex enhancements earn more points (graded at TA discretion)
- Document all new controls and how to use them in a separate `EXTRA_CREDIT.md` file


---

### 3. New Feature [5-30 Points]

Add entirely new functionality to the game that wasn't in the original specification.

**5-Point Examples:**
- Add a "Quick Sell Junk" button that sells all common items at once
- Implement item sorting in inventory (by type, value, rarity)
- Add a combat log that displays recent battle events
- Create a statistics page showing player achievements (total gold earned, enemies defeated, etc.)

**10-30 Point Examples:**
- Implement a crafting system where players can combine items to create new ones
  - Add new `CraftingController` and `CraftingService`
  - Create crafting recipes and UI for the crafting interface
- Add a skill tree or leveling system with special abilities
  - Track XP and allow skill point allocation
  - Create new endpoints for skill management
- Implement a merchant reputation system that affects shop prices
- Create random dungeon events (treasure rooms, trap rooms, merchant encounters)


**Requirements:**
- Feature must include both backend (Java/Spring Boot) and frontend (HTML/CSS/JS) components
- Must integrate with existing game mechanics
- Include comprehensive documentation explaining:
  - What the feature does
  - How to use it
  - What files were added/modified
  - Any new API endpoints created
- Feature must be fully functional and bug-free
- More complex features earn more points (graded at TA discretion)
- Document all new features and how to use them in a separate `EXTRA_CREDIT.md` file


---

### Extra Credit Submission Guidelines

1. **Create a file named `EXTRA_CREDIT.md`** in the root of your project
2. In this file, document:
   - Which extra credit option(s) you implemented
   - Detailed description of what you added/changed
   - List of all files modified or created
   - Instructions for testing your enhancements
3. **Your extra credit work must not break any required functionality**
4. Submit your project normally to Gradescope; TAs will review your `EXTRA_CREDIT.md` file

**Example `EXTRA_CREDIT.md` Structure:**
```markdown
# Extra Credit Submission

## Graphics Enhancement [5 Points]
- Added smooth CSS animations for battle attacks
- Created custom health bar with gradient effects
- Files modified: `style.css`, `app.js`

## New Feature: Crafting System
- Implemented full crafting system allowing item combination
- New files:
  - CraftingController.java
  - CraftingService.java
  - CraftingRecipe.java
- Modified files:
  - app.js (added crafting UI)
  - index.html (added crafting section)
- Instructions: Click "Craft" button in inventory to access crafting interface
```

---

## Total Possible Points

- Base Assignment: 100 points
- Extra Credit: Up to 30 points
- **Maximum Total: 130 points**

---

## Getting Started

### Installation & Setup
#### Option 1: IntelliJ IDEA (Recommended)

1. **Open the Project:**
   - Open IntelliJ IDEA
   - Click `File` → `Open`
   - Navigate to and select the `dungeon_loot_manager` folder
   - Click `Open`

2. **Import as Gradle Project:**
   - IntelliJ should automatically detect it as a Gradle project
   - If prompted, select "Trust Project"
   - Wait for Gradle to sync and download dependencies (watch the progress bar at the bottom)

3. **Configure Project SDK:**
   - Go to `File` → `Project Structure` (or press `Cmd+;` on Mac / `Ctrl+Alt+Shift+S` on Windows)
   - Under "Project", ensure SDK is set to Java 21 or higher
   - Click `Apply` and `OK`

4. **Run the Application:**
   
   **Method 1: Using the Run Configuration**
   - Find the main application class: `Hw09DungeonLootManagerApplication.java`
   - Right-click on the file and select `Run 'Hw09DungeonLootManagerApplication'`
   
   **Method 2: Using Gradle**
   - Open the Gradle tool window (usually on the right side)
   - Navigate to `dungeon_loot_manager` → `Tasks` → `application`
   - Double-click `bootRun`
   
   **Method 3: Using Terminal**
   - Open the terminal at the bottom of IntelliJ
   - Run:
     ```bash
     ./gradlew bootRun
     ```

5. **Stop the Application:**
   - Click the red square "Stop" button in the Run window
   - Or press `Cmd+F2` (Mac) / `Ctrl+F2` (Windows/Linux)

---

#### Option 2: VS Code

1. **Install Required Extensions:**
   - Open VS Code
   - Install the following extensions from the Extensions marketplace:
     - "Extension Pack for Java" (by Microsoft)
     - "Spring Boot Extension Pack" (by VMware)

2. **Open the Project:**
   - Open VS Code
   - Go to `File` → `Open Folder`
   - Select the `dungeon_loot_manager` folder

3. **Let VS Code Configure:**
   - VS Code will automatically detect the Gradle project
   - Wait for the Java extension to finish loading and indexing
   - You should see "Java Projects" in the sidebar

4. **Run the Application:**
   - Open the integrated terminal (`Terminal` → `New Terminal`)
   - Run the following command:
     ```bash
     ./gradlew bootRun
     ```
   - **Or** use the Spring Boot Dashboard:
     - Click the Spring Boot icon in the sidebar
     - Find your application and click the "Play" button

5. **Alternative: Use the Command Palette:**
   - Press `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows/Linux)
   - Type "Spring Boot: Run"
   - Select your application


---

### Testing Your Application

#### Running Locally (Command Line)

Regardless of your IDE, you can always use the command line:

1. **Build the project:**
   ```bash
   ./gradlew build
   ```

2. **Run the application:**
   ```bash
   ./gradlew bootRun
   ```

3. **Access the application:**
   - Frontend: `http://localhost:8080`
   - H2 Database Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:dungeondb`
     - Username: `sa`
     - Password: (leave blank)

4. **Stop the application:**
   - Press `Ctrl+C` in the terminal

### Testing API Endpoints

You can test your endpoints using:
- **Browser** (for GET requests): Navigate to `http://localhost:8080/api/player`
- **curl** (command line):
  ```bash
  curl http://localhost:8080/api/player
  ```
- **Postman** or **Bruno** (GUI tools for API testing)

---


## Turn-In Procedure

### Submission

To submit, upload a **ZIP file of your entire project** to the corresponding assignment on Gradescope. Your ZIP should contain:

- `src/` folder with all Java files
- `build.gradle`
- `gradlew` and `gradlew.bat`
- `gradle/` folder
- `README.md`
- Any other necessary project files

**Do NOT include:**
- `build/` directory
- `bin/` directory
- `.gradle/` directory
- `.idea/` or other IDE-specific files

Make sure you see the message stating the assignment was submitted successfully. You can submit as many times as you want before the deadline. We will only grade your latest submission. **Be sure to submit every file each time you resubmit.**

---

### Gradescope Autograder

The autograder will primarily check if your project **compiles and builds successfully**. 

The Gradescope tests serve two main purposes:
1. Prevent upload mistakes (e.g., non-compiling code)
2. Provide basic build validation

**Note:** The test cases on Gradescope are by no means comprehensive. Manual grading by TAs will assess the correctness of your implementations.

---

## Burden of Testing

You are responsible for thoroughly testing your submission against the written requirements to ensure you have fulfilled the requirements of this assignment. Test your endpoints locally before submitting!

If you have questions about the requirements given, reach out to a TA or Professor via Ed Discussion for clarification.

---

## Allowed Imports

For this Spring Boot assignment, you **may use** the following imports:

- `org.springframework.*` (all Spring Framework classes)
- `jakarta.persistence.*` (JPA annotations)
- `java.util.*` (standard Java utilities)
- Any other standard Java libraries as needed

**You may NOT use:**
- Code generation tools or generative AI (e.g., ChatGPT, Copilot)

---

## Feature Restrictions

Do not use any of the following in your final submission:

- `var` (the reserved keyword)
- `System.exit`

---

## Collaboration

Only discussion of the assignment at a **conceptual high level** is allowed. You can discuss course concepts and assignments broadly; that is, at a conceptual level to increase your understanding. If you find yourself dropping to a level where specific Java code is being discussed, that is going too far.

**You MAY NOT use code generation tools to complete this assignment.** This includes generative AI tools such as ChatGPT or GitHub Copilot.

---

## Important Notes (Don't Skip)

- **Non-compiling projects will receive a 0 for all associated rubric items.**
- Do not submit `.class` files or build artifacts.
- Test your code locally in addition to the basic checks on Gradescope.
- Submit a complete project ZIP each time you resubmit.
- Read the "Allowed Imports" and "Restricted Features" to avoid losing points.
- Check Ed Discussion for official clarifications and announcements.

---

## Academic Integrity

It is expected that everyone will follow the Student-Faculty Expectations document and the Student Code of Conduct. The professor expects a positive, respectful, and engaged academic environment inside the classroom, outside the classroom, in all electronic communications, on all file submissions, and on any document submitted throughout the duration of the course.

No inappropriate language is to be used, and any assignment deemed by the professor to contain inappropriate offensive language or threats will receive a zero. You are to use professionalism in your work. Violations of this conduct policy will be turned over to the Office of Student Integrity for misconduct.


---

Good luck, and have fun exploring the dungeon! 🗡️🛡️

-Kel, Xavier
