# Project Description

# NOTE: ******************

- Do not allow user to delete all shelfPositions. If user try to delete the last shelfPosition of the device, it should
  delete the device also (because what's the point of having a device with 0 shelfPositions)

## Creating Entities/Models

### Device

- Properties -> uniqueId, deviceName, partNumber, buildingName, deviceType, numberOfShelfPositions
- Relationship -> HAS with ShelfPosition
- Constraints:
    - uniqueId -> unique + notNull
    - deviceName -> unique + notNull
    - partNumber -> notNull
    - buildingName -> notNull
    - deviceType -> notNull
    - numberOfShelfPositions -> notNull

### ShelfPosition

- Properties -> uniqueId, deviceId
- Relationship -> HAS with Shelf
- Constraints:
    - uniqueId -> unique + notNull
    - deviceId -> notNull  (here , not adding unique constraint because multiple shelfPositions can be attached to
      single device)

### Shelf

- Properties -> uniqueId, shelfName, partNumber
- Constraints:
    - uniqueId -> unique + notNull
    - shelfName -> unique + notNull
    - partNumber -> notNull

## Creating Database

- Created database using neo4j desktop and adding credentials in appilcation.properties. Putting application.properties
  file in .gitignore to hide/mask credentials

## Created Neo4j Driver

- Created config/Neo4jConfig class. Inside it, created a Bean which return a singleton Neo4j Driver which we can use
  throughout our application.

## Creating Repositories

### DeviceRepository

- Autowired the neo4j driver using constructor injection
- Methods:
    - isDevicePresent() - done
    - createDevice() - done
    - deleteDevice() - done
    - updateDevice() - pending
    - getAllDevice() - done
    - getDeviceDetails() - done

### ShelfRepository

- Autowired the neo4j driver using constructor injection
- Methods:
    - isShelfPresent() - done
    - createShelfAndAttach() - done
    - deleteShelf()  - done

### ShelfPositionRepository

- Autowired the neo4j driver using constructor injection
- Methods:
    - createShelfPositionAndAttach() - done
    - deleteShelfPostion()  - done
    - isShelfPositionPresent() - done

## Creating Services

### DeviceService

- Autowire the DeviceRepository using constructor injection.
- Methods:
    - getAllDevices() - done
    - getDeviceDetails() - done
    - createDevice() - done
    - deleteDevice() - done

### ShelfService

- Autowire the ShelfRepository using constructor injection.
- Methods:
    - createShelfAndAttach() - done
    - deleteShelf() - done

### ShelfPositionService

- Autowire the ShelfPositionRepository using constructor injection
- Methods:
    - createShelfPositionAndAttach() - done
    - deleteShelfPosition() - done

## Creating Controller

### DeviceController

- Make it a restController and autowire deviceService using constructor injection
- Endpoints:
    - POST("/api/devices/") - done
    - GET("/api/devices/allDevices") - done
    - GET("/api/devices/deviceDetails/{deviceName}") - done
    - DELETE("/api/devices/delete/{deviceName}") - done

### ShelfController

- Make it a restController and autowire shelfService using constructor injection
- Endpoints:
    - POST("/api/shelfs/") - done
    - DELETE("/api/shelfs/delete") - done

### ShelfPositionController

- Mate it a restController and autowire shelfPositionService using constructor injection
- Endpoints:
    - POST("/api/shelfPositions/") - done
    - DELETE("/delete/{deviceName}/{shelfPositionId}") - done

# Notes/Learnings:

- While creating entities, it is mandatory to create getters and setters for each property and a noArgsConstructor.
- For validation of enum properties in entities, we should use @NotNull()
- For implementing soft delete feature, we are doing this:
    - For Shelf:
      - 
    - For Device:
      - 

## Shortcuts

- To copy something in next line, click on the line which you want to duplicate in next line (remember , do not select
  any text) and use - Ctrl + D
- Format code - Ctrl + Alt + L
- Select Next Occurrence - Alt + J