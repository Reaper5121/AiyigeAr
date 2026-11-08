# Decision 0001: Automatic Quotations

## Decision

Prepare and display one professional quotation in the app as the first checkout step. After
the customer confirms it, the mobile flow continues to courier selection,
payment selection, and a final order summary. If the customer declines, the app
returns to the screen they started from with their selection intact. The
customer selects J&T or Lalamove.
Staff can print the quotation and record delivery progress after handoff.

## Reason

This removes duplicate manual quotation work and keeps the printed document
consistent with the purchased order snapshot.

## Consequence

Quotation editor controls are intentionally disabled. The mobile app has no
separate `View Quotation` action; the quotation is reviewed before courier and
payment selection. Changes must happen in a new order or through an explicitly
planned data migration, not by editing the printed quotation.
