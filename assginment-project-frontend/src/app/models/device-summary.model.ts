export interface Device {
  id: string;
  deviceName: string;
  partNumber: string;
  deviceType: string;
  buildingName: string;
  noOfShelfPositions: number;
}

export interface ShelfPosition {
  id: string;
  deviceId: string;
}

export interface Shelf {
  id: string;
  shelfName: string;
  partNumber: string;
}

export interface GlobalDeviceSummary {
  device: Device;
  shelfPairs: {
    shelfPosition: ShelfPosition | null;
    shelf: Shelf | null;
  }[];
}