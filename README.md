## Codebase Overview

The application follows a simple command-driven architecture:

### Core Components

- **DBLoader**
  - Executes SQL queries.
  - Converts query results into record objects.

- **Records**
  - Represent the data returned by queries.
  - Each distinct query result format should have its own record class.

- **Printers**
  - Format and display record objects in tables.
  - Typically implemented using the `TablePrinter` utility.

- **Commands**
  - Each CLI command implements the `Command` interface.
  - Responsible for:
    - Collecting user input.
    - Calling the appropriate `DBLoader` method.
    - Displaying results using a printer.

### Utilities

- **ParsingHelper**
  - Simplifies collecting and validating user input.

- **ExceptionHandler**
  - Converts exceptions into user-friendly error messages.

## Adding a New Query

To add a new query to the application:

1. Create a **record class** representing the query result (if returning more than a single value).
2. Create a corresponding **printer class** for displaying the result.
3. Add a method to **DBLoader** that:
   - Executes the SQL query.
   - Converts the result set into record objects.
4. Create a new **Command** implementation that:
   - Gets user input.
   - Calls the `DBLoader` method.
   - Prints the results.
   - Handles exceptions through `ExceptionHandler`.
   - Defines its command keyword and description.
5. Register the command by adding it to the command list in `CommandExecutor`.

### Query Workflow

```text
User Input
    ↓
Command
    ↓
DBLoader
    ↓
SQL Query
    ↓
ResultSet
    ↓
Record Object(s)
    ↓
Printer
    ↓
Console Output
```


### database_population/populate.py

Populates the SQL Server database from the cleaned Winnipeg road network datasets. The script creates all tables defined in `sql/create-tables.sql`, validates and transforms the source CSV data, and inserts records into the database using batched operations for improved performance. Data is inserted in batches of 5,000 records using `executemany()` to improve loading performance and reduce database transaction overhead.

Data loaded includes roads, segments, speed limits, school zones, plow zones, neighbourhoods, school divisions, wards, and addresses, along with their associated geographic point data.