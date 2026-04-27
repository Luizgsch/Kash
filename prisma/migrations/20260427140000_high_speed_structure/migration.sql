-- High-speed structure: INFLOW/OUTFLOW + Transaction.walletId

ALTER TYPE "TransactionType" RENAME VALUE 'INCOME' TO 'INFLOW';
ALTER TYPE "TransactionType" RENAME VALUE 'EXPENSE' TO 'OUTFLOW';

ALTER TABLE "Transaction" ADD COLUMN "walletId" TEXT;

UPDATE "Transaction" t
SET "walletId" = c."walletId"
FROM "Category" c
WHERE c."id" = t."categoryId";

ALTER TABLE "Transaction" ALTER COLUMN "walletId" SET NOT NULL;

ALTER TABLE "Transaction" ADD CONSTRAINT "Transaction_walletId_fkey" FOREIGN KEY ("walletId") REFERENCES "Wallet"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

CREATE INDEX "Transaction_walletId_idx" ON "Transaction"("walletId");
