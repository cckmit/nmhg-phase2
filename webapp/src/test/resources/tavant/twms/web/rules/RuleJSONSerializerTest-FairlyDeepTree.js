{
  "right": {
    "type": "RULE_FRAGMENT",
    "description": "  Claim type  is \"Parts\" ",
    "name": "When claim type is 'Parts'",
    "id": 2
  },
  "type": "OPERATOR",
  "left": {
    "right": {
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
    "type": "OPERATOR",
    "left": {
      "type": "RULE_FRAGMENT",
      "description": "  Inventory Item Type  is \"RETAIL\" ",
      "name": "Claim's Inventory Item is retailed",
      "id": 1
    },
    "name": " and "
  },
  "name": " or "
}