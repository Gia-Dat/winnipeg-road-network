Hello test

### database_population/populate.py

Populates the SQL Server database from the cleaned Winnipeg road network datasets. The script creates all tables defined in `sql/create-tables.sql`, validates and transforms the source CSV data, and inserts records into the database using batched operations for improved performance.

Data loaded includes roads, segments, speed limits, school zones, plow zones, neighbourhoods, school divisions, wards, and addresses, along with their associated geographic point data.