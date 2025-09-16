# Drones Dispatch Controller

Spring Boot 3 (Java 17) REST API for managing a fleet of drones and medication loading.

## Requirements

- Java 17 or newer (`java -version`)
- Maven 3.9+ (on Windows you can use Chocolatey Maven path)
  - Example path: `C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.11\bin\mvn.cmd`

### Quick Start (Windows PowerShell)

- From the project root `C:\Users\Sir_Cliff\Desktop\zb-drones`:

```powershell
# Option A: If mvn is not on PATH, use full path
& 'C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.11\bin\mvn.cmd' clean spring-boot:run

# Option B: If mvn is on PATH
mvn clean spring-boot:run
```

- App starts on `http://localhost:8080`.
- Home page: `http://localhost:8080/` (lists key endpoints)
- H2 console: `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:dronesdb`
  - Username: `sa`
  - Password: (leave empty)

### Register and Load (quick commands)

PowerShell (recommended on Windows):

```powershell
# Register a new drone{you can differentiate serial numbers as needed}
$registerBody = @{
  serialNumber = 'DRN-2001'
  model = 'MIDDLEWEIGHT'
  weightLimitGrams = 500
  batteryPercentage = 90
  state = 'IDLE'
} | ConvertTo-Json
Invoke-RestMethod -Method POST http://localhost:8080/api/drones -ContentType 'application/json' -Body $registerBody | ConvertTo-Json -Depth 5

# Load medications by code onto that drone
$loadBody = '["MED_1","MED_2"]'
Invoke-RestMethod -Method POST http://localhost:8080/api/drones/DRN-2001/load -ContentType 'application/json' -Body $loadBody | ConvertTo-Json -Depth 5

# Verify loaded medications
Invoke-RestMethod -Method GET http://localhost:8080/api/drones/DRN-2001/medications | ConvertTo-Json -Depth 5
```

curl:

```bash
# Register a new drone
curl -X POST http://localhost:8080/api/drones \
  -H "Content-Type: application/json" \
  -d '{
    "serialNumber": "DRN-2001",
    "model": "MIDDLEWEIGHT",
    "weightLimitGrams": 500,
    "batteryPercentage": 90,
    "state": "IDLE"
  }'

# Load medications by code onto that drone
curl -X POST http://localhost:8080/api/drones/DRN-2001/load \
  -H "Content-Type: application/json" \
  -d '["MED_1","MED_2"]'

# Verify loaded medications
curl http://localhost:8080/api/drones/DRN-2001/medications
```

### H2 Console: example SQL

Use these examples in the H2 Console to inspect or tweak in-memory data.

```sql
-- List tables
SHOW TABLES;
-- or
SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC';

-- Describe table columns
SHOW COLUMNS FROM DRONE;
-- or
SELECT COLUMN_NAME, TYPE_NAME, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'DRONE';

-- View all drones
SELECT * FROM DRONE;

-- Filters
SELECT * FROM DRONE WHERE STATE = 'IDLE';
SELECT * FROM DRONE WHERE BATTERYPERCENTAGE < 25;
SELECT * FROM DRONE WHERE WEIGHTLIMITGRAMS >= 500 AND BATTERYPERCENTAGE >= 25;

-- Find one drone by serial
SELECT * FROM DRONE WHERE SERIALNUMBER = 'DRN-1001';

-- List medications
SELECT * FROM MEDICATION;

-- Medications loaded on a specific drone
SELECT m.*
FROM DRONE d
JOIN DRONE_MEDICATIONS dm ON dm.DRONE_ID = d.ID
JOIN MEDICATION m ON m.ID = dm.MEDICATION_ID
WHERE d.SERIALNUMBER = 'DRN-1001';

-- Drones and count of loaded meds
SELECT d.SERIALNUMBER, COUNT(dm.MEDICATION_ID) AS MED_COUNT
FROM DRONE d
LEFT JOIN DRONE_MEDICATIONS dm ON dm.DRONE_ID = d.ID
GROUP BY d.SERIALNUMBER
ORDER BY MED_COUNT DESC;

-- Total loaded weight per drone
SELECT d.SERIALNUMBER, COALESCE(SUM(m.WEIGHTGRAMS), 0) AS TOTAL_WEIGHT
FROM DRONE d
LEFT JOIN DRONE_MEDICATIONS dm ON dm.DRONE_ID = d.ID
LEFT JOIN MEDICATION m ON m.ID = dm.MEDICATION_ID
GROUP BY d.SERIALNUMBER
ORDER BY TOTAL_WEIGHT DESC;

-- Available drones for loading (DB-side approximation)
SELECT * FROM DRONE
WHERE BATTERYPERCENTAGE >= 25 AND STATE = 'IDLE';

-- Battery audit: latest samples
SELECT *
FROM BATTERYAUDIT
ORDER BY DRONESERIALNUMBER, RECORDEDAT DESC;

-- Latest sample for one drone
SELECT *
FROM BATTERYAUDIT
WHERE DRONESERIALNUMBER = 'DRN-1001'
ORDER BY RECORDEDAT DESC
FETCH FIRST 1 ROW ONLY;

-- Insert a new medication
INSERT INTO MEDICATION (NAME, WEIGHTGRAMS, CODE, IMAGEURL)
VALUES ('PainRelief', 120, 'MED_PR120', NULL);

-- Attach a medication to a drone (replace IDs as needed)
-- SELECT ID FROM DRONE WHERE SERIALNUMBER = 'DRN-1001';
-- SELECT ID FROM MEDICATION WHERE CODE = 'MED_PR120';
INSERT INTO DRONE_MEDICATIONS (DRONE_ID, MEDICATION_ID) VALUES (1, 9);

-- Update drone state or battery
UPDATE DRONE SET STATE = 'LOADING' WHERE SERIALNUMBER = 'DRN-1001';
UPDATE DRONE SET BATTERYPERCENTAGE = 55 WHERE SERIALNUMBER = 'DRN-1001';

-- Remove a medication from a drone
DELETE FROM DRONE_MEDICATIONS WHERE DRONE_ID = 1 AND MEDICATION_ID = 9;
```

Notes:

- Table and column names are unquoted; H2 uppercases them by default: `DRONE`, `MEDICATION`, `BATTERYAUDIT`, `DRONE_MEDICATIONS`.
- Enum fields `STATE` and `MODEL` store strings like `IDLE`, `MIDDLEWEIGHT`, etc.

### What gets preloaded

- 10 drones with serials `DRN-1001` ... `DRN-1010` in `IDLE` state, battery 40/60/80%.
- 8 medications with codes `MED_1` ... `MED_8`.

### API Endpoints

Base URL: `http://localhost:8080`

- Home
  - GET `/` → Simple info page

- Register a drone
  - POST `/api/drones`
  - Body (JSON):

```json
{
  "serialNumber": "DRN-2001",
  "model": "MIDDLEWEIGHT",
  "weightLimitGrams": 500,
  "batteryPercentage": 90,
  "state": "IDLE"
}
```

- Load medications onto a drone
  - POST `/api/drones/{serial}/load`
  - Body (JSON): array of medication codes

```json
["MED_1", "MED_2"]
```

- Check loaded medications for a drone
  - GET `/api/drones/{serial}/medications`

- List available drones for loading
  - GET `/api/drones/available`
  - GET `/api/drones` (same list for convenience)

- Check drone battery level
  - GET `/api/drones/{serial}/battery`

### Example Requests

PowerShell (Invoke-RestMethod):

```powershell
# Info page
Invoke-RestMethod -Method GET http://localhost:8080/

# Available drones
Invoke-RestMethod -Method GET http://localhost:8080/api/drones/available | ConvertTo-Json -Depth 5

# Also available at /api/drones
Invoke-RestMethod -Method GET http://localhost:8080/api/drones | ConvertTo-Json -Depth 5

# Battery level
Invoke-RestMethod -Method GET http://localhost:8080/api/drones/DRN-1001/battery

# Load medications
$body = '["MED_1","MED_2"]'
Invoke-RestMethod -Method POST http://localhost:8080/api/drones/DRN-1001/load -ContentType 'application/json' -Body $body

# Check loaded medications
Invoke-RestMethod -Method GET http://localhost:8080/api/drones/DRN-1001/medications | ConvertTo-Json -Depth 5
```

curl:

```bash
curl http://localhost:8080/
curl http://localhost:8080/api/drones/available
curl http://localhost:8080/api/drones
curl http://localhost:8080/api/drones/DRN-1001/battery
curl -X POST http://localhost:8080/api/drones/DRN-1001/load -H "Content-Type: application/json" -d '["MED_1","MED_2"]'
curl http://localhost:8080/api/drones/DRN-1001/medications
```

### Business Rules enforced

- Drone `weightLimitGrams` ≤ 500.
- Cannot load if total medication weight exceeds drone limit.
- Cannot be in `LOADING`/load operation when battery < 25%.
- States used: `IDLE`, `LOADING`, `LOADED`, `DELIVERING`, `DELIVERED`, `RETURNING`.

### Battery Audit

- A background task periodically records each drone's battery level to `BatteryAudit` table.
- Interval is configurable via `application.properties`:

```properties
drones.audit.interval-ms=60000
```

### Build, Test, Package

```powershell
# Compile
& 'C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.11\bin\mvn.cmd' clean compile

# Run tests (none required, placeholder ready)
& 'C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.11\bin\mvn.cmd' test

# Create jar
& 'C:\ProgramData\chocolatey\lib\maven\apache-maven-3.9.11\bin\mvn.cmd' clean package

# Run the built jar
java -jar target/drones-1.0.0.jar
```

### Errors and common responses

- 404 Not Found: wrong path or missing resource (e.g., serial not found)
- 405 Method Not Allowed: wrong HTTP verb (e.g., GET to a POST endpoint)
- 400/409 validation: weight exceeds limit, or battery < 25% during load

### Troubleshooting

- "mvn is not recognized": use the full Maven path shown above or add it to PATH.
- Port in use: change `server.port` in `src/main/resources/application.properties`.
- H2 login fails: ensure JDBC URL `jdbc:h2:mem:dronesdb`, user `sa`, empty password.
