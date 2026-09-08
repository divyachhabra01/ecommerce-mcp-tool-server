# 🛒 E-Commerce MCP Tool Server

An MCP-enabled backend for an e-commerce store, built with **Spring Boot**, **Spring AI**, and the **Model Context Protocol (MCP)**. It re-implements the business logic of a traditional e-commerce REST application — products, cart, orders, customers — but exposes it as a set of **MCP tools** rather than REST endpoints, so any MCP-compatible AI agent (Claude, or another MCP client) can browse products, manage a cart, and place orders on a customer's behalf through natural language.

> Originally built as a college e-commerce project (Spring Boot + JPA business logic for products, cart, and orders). Rebuilt here as an **MCP tool server**, turning that same business logic into callable tools for an LLM-based agent.

---

## 🎥 Demo

Watch demo here : https://github.com/user-attachments/assets/979a70f8-2f16-4129-950c-a73dd83d3832

---

## ✨ What it does

The agent can, entirely through conversation:

- **Search and compare products** by keyword, category, or brand
- **Check inventory** and find in-stock alternatives to a given product
- **Compare multiple products** side-by-side
- **Manage a customer's cart** — add, remove, view, or clear items
- **Place orders**, look up past orders, and check/cancel eligible orders
- **Look up customers** by ID or email

## 🧠 Architecture

```text
AI Agent (e.g. Claude, or any MCP client)
        │  (stdio / MCP protocol)
        ▼
E-Commerce MCP Tool Server
        │
        ├── ProductMcpTools    → search_products, get_product, check_inventory,
        │                        find_alternatives, compare_products
        ├── CartMcpTools       → get_cart, add_to_cart, remove_from_cart, clear_cart
        ├── OrderMcpTools      → place_order, get_orders, get_order_details,
        │                        check_cancellation_eligibility, cancel_order
        └── CustomerMcpTools   → get_customers, get_customer, find_customer_by_email
        │
        ▼
Service Layer (ProductService, CartService, OrderService, CustomerService)
        │
        ▼
Spring Data JPA Repositories
        │
        ▼
H2 Database (file-based, seeded via data.sql)
```

Each MCP tool is a thin `@McpTool`-annotated method that delegates to the existing service layer — the original e-commerce business logic is untouched; only the entry point changed from HTTP controllers to MCP tool calls.

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.1 |
| AI / MCP | Spring AI 2.0.1 |
| MCP Server | `spring-ai-starter-mcp-server` |
| Transport | MCP over STDIO |
| Persistence | Spring Data JPA + H2 |
| Build Tool | Maven |
| AI Client | Claude Desktop |
| Other | Lombok |

## 🚀 Getting Started

### Prerequisites

Make sure the following are installed:

- Java 21+
- Git
- Claude Desktop (or another MCP-compatible client)
- Maven is optional — the project includes the Maven Wrapper

Verify Java:

```bash
java -version
```

The project uses Java 21.

### 1. Clone the Repository

```bash
git clone https://github.com/<your-username>/ecommerce-mcp-tool-server.git
cd ecommerce-mcp-tool-server
```

### 2. Configure the H2 Database

The application uses a **file-based H2 database**.

The database is stored inside the project under:

```text
data/ecommerce
```

The datasource configuration is located in:

```text
src/main/resources/application.properties
```

Use an **absolute path** for the H2 database:

```properties
spring.datasource.url=jdbc:h2:file:/absolute/path/to/ecommerce-mcp-tool-server/data/ecommerce;AUTO_SERVER=TRUE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

Replace:

```text
/absolute/path/to/ecommerce-mcp-tool-server
```

with the actual location of your cloned project.

For example:

```properties
spring.datasource.url=jdbc:h2:file:/Users/yourname/IdeaProjects/ecommerce-ai-agent/data/ecommerce;AUTO_SERVER=TRUE
```

Sample product and customer data is loaded from:

```text
src/main/resources/data.sql
```

The database is persistent, so the data remains available between application restarts.

> **Important:** Use an absolute path for the H2 database. Claude Desktop launches the MCP server as a separate process, so a relative database path can cause H2 to create or look for the database in an unexpected location.

### 3. Build the Application

The project includes the Maven Wrapper, so Maven does not need to be installed globally.

On macOS/Linux:

```bash
./mvnw clean package
```

On Windows:

```bash
mvnw.cmd clean package
```

After a successful build, the executable JAR will be created in:

```text
target/ecommerce-ai-agent-0.0.1-SNAPSHOT.jar
```

### 4. Configure Claude Desktop

This application runs as an **MCP server over STDIO**.

Claude Desktop starts the Spring Boot application as a separate process and communicates with it using the MCP protocol.

On macOS, open:

```text
~/Library/Application Support/Claude/claude_desktop_config.json
```

Add the following configuration:

```json
{
  "mcpServers": {
    "ecommerce": {
      "command": "/usr/bin/java",
      "args": [
        "-jar",
        "/absolute/path/to/ecommerce-ai-agent/target/ecommerce-ai-agent-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

Replace:

```text
/absolute/path/to/ecommerce-ai-agent/
```

with the actual path to your project.

For example:

```json
{
  "mcpServers": {
    "ecommerce": {
      "command": "/usr/bin/java",
      "args": [
        "-jar",
        "/Users/yourname/IdeaProjects/ecommerce-ai-agent/target/ecommerce-ai-agent-0.0.1-SNAPSHOT.jar"
      ]
    }
  }
}
```

Save the configuration and completely restart Claude Desktop.

### 5. Start the MCP Server

When using Claude Desktop, **you do not need to manually start the JAR**.

Claude Desktop starts the MCP server using the configuration above.

The flow is:

```text
Claude Desktop
      ↓
Starts Java JAR
      ↓
Spring Boot Application
      ↓
Spring AI MCP Server
      ↓
MCP Tools
      ↓
E-commerce Services
      ↓
H2 Database
```

The application is configured as a non-web Spring Boot application:

```properties
spring.main.web-application-type=none
```

Therefore, it does not start a traditional HTTP/Tomcat server.

### 6. Verify the MCP Connection

After restarting Claude Desktop, the `ecommerce` MCP server should be available.

Try asking Claude:

```text
Search for laptops in my ecommerce store.
```

Claude should discover and invoke the appropriate MCP tools.

You can also test:

```text
Check the inventory for product ID 1.
```

or:

```text
Show me customer ID 1's shopping cart.
```

If Claude successfully uses the `ecommerce` integration and returns data from the application, the MCP server is connected correctly.

### 7. Running the Application Without Claude Desktop

For development or troubleshooting, the application can also be started directly from the terminal:

```bash
./mvnw spring-boot:run
```

or:

```bash
java -jar target/ecommerce-ai-agent-0.0.1-SNAPSHOT.jar
```

However, because this is a **STDIO MCP server**, the normal usage pattern is to let the MCP client, such as Claude Desktop, start and manage the process.

> **Important:** Do not manually start another copy of the application while Claude Desktop is already running the MCP server, because both processes may attempt to access the same file-based H2 database.

## 💬 Example Interaction (via an MCP-connected AI agent)

```text
User: "I need a laptop for programming under 70000"
Agent: [search_products] → compares specs and price
       → Recommends the Dell Inspiron 15 (i5, 16GB RAM, 512GB SSD) as best value

User: "Add that to customer 1's cart"
Agent: [add_to_cart] → Cart updated, total ₹64,999

User: "Place the order"
Agent: [place_order] → Order #4 confirmed, status: PLACED
```

## 🔧 MCP Tools

### Product Tools

```text
search_products
get_product
check_inventory
find_alternatives
compare_products
```

### Cart Tools

```text
get_cart
add_to_cart
remove_from_cart
clear_cart
```

### Order Tools

```text
place_order
get_orders
get_order_details
check_cancellation_eligibility
cancel_order
```

### Customer Tools

```text
get_customers
get_customer
find_customer_by_email
```

## 📂 Project Structure

```text
ecommerce-mcp-tool-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/ecommerceai/
│   │   │       ├── mcp/            # MCP tool definitions
│   │   │       ├── service/        # Core business logic
│   │   │       ├── repository/     # Spring Data JPA repositories
│   │   │       ├── entity/         # Domain entities
│   │   │       └── dto/            # Response DTOs
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       └── data.sql
│   │
│   └── test/
│
├── data/                            # Local H2 database - not committed
├── target/                          # Maven build output - not committed
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## 🗄️ Database

The application uses a **file-based H2 database**.

Database location:

```text
data/ecommerce
```

Sample data is initialized from:

```text
src/main/resources/data.sql
```

The database uses:

```text
AUTO_SERVER=TRUE
```

to support H2 mixed-mode access when required.

The local database files should **not** be committed to GitHub.

Recommended `.gitignore`:

```gitignore
# Maven
target/

# IntelliJ IDEA
.idea/
*.iws
*.iml
*.ipr

# macOS
.DS_Store

# H2 Database
data/
*.mv.db
*.trace.db
*.lock.db

# Logs
*.log
logs/

# Environment / Local Config
.env
.env.*
application-local.properties
application-local.yml

# Temporary files
*.swp
*.swo
```

The Maven Wrapper files should be committed:

```text
mvnw
mvnw.cmd
.mvn/wrapper/maven-wrapper.properties
```

## 🤖 How MCP Fits Into the Application

The original application was designed around traditional backend business logic:

```text
REST Controller
      ↓
Service Layer
      ↓
Repository
      ↓
Database
```

The MCP version adds an AI-callable interface:

```text
AI Agent
    ↓
MCP Protocol
    ↓
MCP Tool
    ↓
Service Layer
    ↓
Repository
    ↓
Database
```

This means the existing business logic does not need to be rewritten for the AI agent.

For example:

```text
User:
"Find me a laptop under 70000 that is currently in stock."

        ↓

AI Agent
        ↓

search_products
        ↓

Product Service
        ↓

Product Repository
        ↓

H2 Database
        ↓

Product information
        ↓

AI Agent
        ↓

Natural-language response
```

The MCP layer therefore acts as an **AI-callable interface over the existing e-commerce backend**.

## 📌 Key Learning

This project demonstrates how an existing Spring Boot backend can be exposed to an AI agent using the **Model Context Protocol (MCP)**.

Instead of creating a completely separate AI backend, the application keeps the existing:

- Domain models
- Business logic
- Services
- Repositories
- Database

and adds an MCP tool layer on top.

This makes the e-commerce functionality available to an AI agent through structured tool calls while keeping the underlying application architecture clean and reusable.

## 📄 License

MIT

---
## 👤 Author

**Divya Chhabra**  
🔗 [LinkedIn](https://www.linkedin.com/in/divya-chhabra-90b009212/)
