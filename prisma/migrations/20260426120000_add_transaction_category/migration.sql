-- AlterTable
ALTER TABLE "Transaction" ADD COLUMN "category" TEXT;

-- CreateIndex
CREATE INDEX "Transaction_organizationId_createdAt_idx" ON "Transaction"("organizationId", "createdAt");
