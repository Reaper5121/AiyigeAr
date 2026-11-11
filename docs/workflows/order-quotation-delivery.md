# Order, Quotation, and Delivery Workflow

1. Customer selects catalog or AR-customized products in the mobile app.
2. AR may save a room capture reference, but does not create an order.
3. When Supabase is configured, the backend verifies the customer's Supabase session and account email before preparing a professional quotation from current inventory prices. AR selections keep their chosen size, finish, colour, add-ons, and notes in the immutable quotation snapshot; unsupported add-ons and products missing from inventory are rejected. Local development without Supabase keeps its offline account mode.
   The app displays it and stores an in-app notification; no email or courier booking is implied.
   Product pricing records specify whether the unit price is per piece or per meter.
   Piece-priced line totals use unit price � quantity; meter-priced lines also multiply
   by panel length in meters. Quotations label unit prices separately from line totals.
4. Customer confirms the quotation. If they change their mind, the app asks for
   confirmation, saves the quote as unfinished through the shared backend, and
   shows it in the burger menu and Settings → My quotations. The customer also
   receives an in-app notification and can resume checkout from the menu or
   notification. The burger menu contains unfinished quotes; Settings → My
   quotations contains the full quotation history.
5. Customer explicitly selects J&T or Lalamove for each checkout; neither is
   preselected.
   Courier booking and its fee are handled by the customer outside the system;
   delivery cost is not included in the quotation or customer payment total.
6. Customer explicitly selects a configured payment method for each checkout;
   the app does not preselect Cash, GCash, or bank transfer.
7. Customer reviews the customer details, selected courier and payment method,
   each quoted item, totals, and the fact that external delivery is excluded.
   Customer confirms and submits the order.
8. The backend links the prepared quotation to the confirmed order and creates
   order/payment records from that same snapshot. Repeated submission returns
   the existing records instead of creating a duplicate order. The selected
   payment method is recorded as pending; mobile checkout does not process or
   verify funds. Unpaid orders remain in the payment review queue and are
   excluded from staff order preparation and courier operations.
   Mobile checkout displays the amount due and method-specific manual guidance.
   Aiyige must provide GCash or bank transfer details; no payment gateway or
   merchant account is configured, so the app does not claim to charge or
   verify funds.
9. Staff can print the customer-confirmed quotation as an order preparation copy
   only when the quotation is linked to its customer-confirmed order, a
   customer-selected payment method is saved, and full payment has been
   recorded as received. Payment method selection in mobile checkout is not
   payment confirmation.
10. After full payment is recorded, the order enters Preparing automatically
    and appears in staff order preparation and courier operations. Staff then
    updates Handed to courier and Delivered. Courier booking and quotation
    attachment are outside the staff workflow.

The customer-facing reference is the quotation number (`QT-...`). The backend
order ID remains an internal reference; payment, order history, and quotation
history match both references so recording payment updates the correct customer
order.

The mobile checkout does not provide a separate `View Quotation` action. The
quotation is the first checkout step and appears automatically in the app.

The customer quotation uses the same backend `items_snapshot`, customer,
business, issue date, and totals as the staff preparation printout. Unfinished
checkout state is stored on the shared quotation record through
`POST /api/quotations/:quotationNumber/checkout-state`; submitting the order
clears the unfinished state and its in-app notification.
Any quotation without a linked order remains available as unfinished checkout,
including quotations marked in progress or confirmed but not yet submitted.
This means Android process death does not need to run a cleanup callback for
the quotation to remain resumable. Once order submission links the quotation to
an order, it leaves the unfinished list and appears in order history. The mobile
app stores each customer's last checkout step, selected courier, and selected
payment method locally per quotation; resuming returns to that step. Draft
progress is cleared after order submission or quotation deletion.

From the burger menu, tapping an unfinished quotation resumes checkout. **Edit
items** restores its saved items to the cart; preparing a replacement quotation
uses current catalog prices, then removes the old unfinished quotation. If the
replacement cannot be prepared, the old quotation remains. **Delete** asks for
confirmation and uses `DELETE /api/quotations/:quotationNumber`; the backend
allows removal only for that signed-in customer's unfinished quote with no
linked order. The full quotation history remains under Settings → My quotations.

Manual quotation creation, editing, and sending are not part of this workflow.

The admin app shows a shared loading indicator while workspace data is loaded
or saved. A blocked preparation print opens a confirmation dialog with an OK
button and explains which customer or payment step is still required.
The mobile app shows a blocking progress indicator while preparing a quotation,
submitting an order, or sending account confirmation, and clears the indicator
when the operation finishes or fails.
