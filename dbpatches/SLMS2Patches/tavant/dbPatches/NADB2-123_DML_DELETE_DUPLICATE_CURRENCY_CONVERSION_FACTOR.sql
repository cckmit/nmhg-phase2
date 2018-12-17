--Chetan
DELETE
FROM currency_conversion_factor
WHERE rowid IN
  (SELECT MAX(ccf.rowid)
  FROM currency_conversion_factor ccf,
    currency_exchange_rate cxr
  WHERE ccf.parent = cxr.id
  GROUP BY ccf.from_date,
    ccf.till_date,
    ccf.factor,
    cxr.from_currency,
    cxr.to_currency
  HAVING COUNT(1) > 1
  )
/
commit
/