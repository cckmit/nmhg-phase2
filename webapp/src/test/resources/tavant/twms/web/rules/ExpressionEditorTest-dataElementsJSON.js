[
  {
    "allowedOperators": [
      " is ",
      " is in parts watch list ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "Item",
    "expression": "claim.serviceInformation.causalPart",
    "name": "Claim's Causal Part"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.serviceInformation.causalPart.make",
    "name": "Claim's Causal Part's Make"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.serviceInformation.causalPart.model",
    "name": "Claim's Causal Part's Model"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.serviceInformation.causalPart.number",
    "name": "Claim's Causal Part's Number"
  },
  {
    "allowedOperators": [
      " is ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "Dealership",
    "expression": "claim.forDealer",
    "name": "Claim's Dealer"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.forDealer.country",
    "name": "Claim's Dealer's Country"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.forDealer.state",
    "name": "Claim's Dealer's State"
  },
  {
    "allowedOperators": [
      " is ",
      " is greater than ",
      " is greater than or equal to ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is less than ",
      " is less than or equal to ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "integer",
    "expression": "claim.hoursInService",
    "name": "Claim's Equipment Hours In Service/Energy Unit"
  },
  {
    "allowedOperators": [
      " is ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "InventoryItem",
    "expression": "claim.itemReference.referredInventoryItem",
    "name": "Claim's Equipment Inventory Item"
  },
  {
    "allowedOperators": [
      " is ",
      " is greater than ",
      " is greater than or equal to ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is less than ",
      " is less than or equal to ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "integer",
    "expression": "claim.itemReference.referredInventoryItem.hoursInService",
    "name": "Claim's Equipment Inventory Item's Hours In Service/Energy Unit"
  },
  {
    "allowedOperators": [
      " is ",
      " is in parts watch list ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "Item",
    "expression": "claim.itemReference.referredInventoryItem.ofType",
    "name": "Claim's Equipment Inventory Item's Of Type"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.referredInventoryItem.ofType.make",
    "name": "Claim's Equipment Inventory Item's Of Type's Make"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.referredInventoryItem.ofType.model",
    "name": "Claim's Equipment Inventory Item's Of Type's Model"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.referredInventoryItem.ofType.number",
    "name": "Claim's Equipment Inventory Item's Of Type's Number"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.referredInventoryItem.serialNumber",
    "name": "Claim's Equipment Inventory Item's Serial Number"
  },
  {
    "allowedOperators": [
      " is ",
      " is in parts watch list ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "Item",
    "expression": "claim.itemReference.{ referredItem!=null ? referredItem : referredInventoryItem.ofType } ",
    "name": "Claim's Equipment Item"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.{ referredItem!=null ? referredItem : referredInventoryItem.ofType } .make",
    "name": "Claim's Equipment Item's Make"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.{ referredItem!=null ? referredItem : referredInventoryItem.ofType } .model",
    "name": "Claim's Equipment Item's Model"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.itemReference.{ referredItem!=null ? referredItem : referredInventoryItem.ofType } .number",
    "name": "Claim's Equipment Item's Number"
  },
  {
    "allowedOperators": [
      " is ",
      " is after ",
      " is before ",
      " is on or after ",
      " is on or before",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "date",
    "expression": "claim.failureDate",
    "name": "Claim's Failure Date"
  },
  {
    "allowedOperators": [
      " is ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "OEMPartReplaced",
    "expression": "claim.serviceInformation.serviceDetail.oEMPartsReplaced",
    "name": "Claim's OEM Parts Replaced"
  },
  {
    "allowedOperators": [
      " is ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "ClaimPayment",
    "expression": "claim.payment",
    "name": "Claim's Payment"
  },
  {
    "allowedOperators": [
      " is ",
      " is greater than ",
      " is greater than or equal to ",
      " is less than ",
      " is less than or equal to ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "money",
    "expression": "claim.payment.claimedAmount",
    "name": "Claim's Payment's Claimed Amount"
  },
  {
    "allowedOperators": [
      " is ",
      " is greater than ",
      " is greater than or equal to ",
      " is less than ",
      " is less than or equal to ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "money",
    "expression": "claim.payment.totalAmount",
    "name": "Claim's Payment's Total Amount"
  },
  {
    "allowedOperators": [
      " is ",
      " is after ",
      " is before ",
      " is on or after ",
      " is on or before",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "date",
    "expression": "claim.repairDate",
    "name": "Claim's Repair Date"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "claim.type",
    "name": "Claim's Type"
  },
  {
    "allowedOperators": [
      " is ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is not  ",
      " starts with "
    ],
    "type": "VARIABLE",
    "datatype": "string",
    "expression": "policy.warrantyType.type",
    "name": "Policy's Policy Type"
  },
  {
    "allowedOperators": [
      " is ",
      " is greater than ",
      " is greater than or equal to ",
      " is not one of ",
      " is not set ",
      " is one of ",
      " is less than ",
      " is less than or equal to ",
      " is not  "
    ],
    "type": "VARIABLE",
    "datatype": "integer",
    "expression": "policy.coverageTerms.serviceHoursCovered",
    "name": "Policy's Hours in Service/Energy Unit covered"
  }
]