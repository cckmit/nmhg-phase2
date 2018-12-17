{
  "type": "OPERATOR",
  "nodes": [
    {
      "right": {
        "value": "Machine",
        "type": "CONSTANT",
        "datatype": "string"
      },
      "type": "EXPRESSION",
      "left": {
        "type": "VARIABLE",
        "datatype": "string",
        "expression": "claim.type",
        "name": "Claim's Type"
      },
      "name": " is "
    },
    {
      "right": {
        "value": "CA2646",
        "type": "CONSTANT",
        "datatype": "string"
      },
      "type": "EXPRESSION",
      "left": {
        "type": "VARIABLE",
        "datatype": "string",
        "expression": "claim.itemReference.referredInventoryItem.serialNumber",
        "name": "Claim's Equipment Inventory Item's Serial Number"
      },
      "name": " is "
    },
    {
      "right": {
        "value": "CA2643",
        "type": "CONSTANT",
        "datatype": "string"
      },
      "type": "EXPRESSION",
      "left": {
        "type": "VARIABLE",
        "datatype": "string",
        "expression": "claim.itemReference.referredInventoryItem.serialNumber",
        "name": "Claim's Equipment Inventory Item's Serial Number"
      },
      "name": " is "
    }
  ],
  "name": " any "
}