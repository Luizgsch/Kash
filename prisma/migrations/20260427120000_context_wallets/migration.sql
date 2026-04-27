-- CreateTable
CREATE TABLE "Wallet" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "organizationId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Wallet_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "Category" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "isFavorite" BOOLEAN NOT NULL DEFAULT false,
    "walletId" TEXT NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "Category_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE INDEX "Wallet_organizationId_idx" ON "Wallet"("organizationId");

-- CreateIndex
CREATE INDEX "Category_walletId_idx" ON "Category"("walletId");

-- CreateIndex
CREATE UNIQUE INDEX "Category_walletId_name_key" ON "Category"("walletId", "name");

-- AddForeignKey
ALTER TABLE "Wallet" ADD CONSTRAINT "Wallet_organizationId_fkey" FOREIGN KEY ("organizationId") REFERENCES "Organization"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "Category" ADD CONSTRAINT "Category_walletId_fkey" FOREIGN KEY ("walletId") REFERENCES "Wallet"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AlterTable
ALTER TABLE "Transaction" ADD COLUMN "categoryId" TEXT;

-- Seed Loja / Pessoal + categories per organization
DO $$
DECLARE
  org RECORD;
  wid_loja TEXT;
  wid_pessoal TEXT;
BEGIN
  FOR org IN SELECT id FROM "Organization"
  LOOP
    wid_loja := gen_random_uuid()::text;
    wid_pessoal := gen_random_uuid()::text;

    INSERT INTO "Wallet" ("id", "name", "organizationId", "createdAt", "updatedAt")
    VALUES (wid_loja, 'Loja', org.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    INSERT INTO "Wallet" ("id", "name", "organizationId", "createdAt", "updatedAt")
    VALUES (wid_pessoal, 'Pessoal', org.id, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    INSERT INTO "Category" ("id", "name", "isFavorite", "walletId", "createdAt", "updatedAt") VALUES
      (gen_random_uuid()::text, 'Venda de Planta', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Fósforo', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Café', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Água', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Embalagem', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Delivery', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Pedido Online', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Desconto', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Ajuste', true, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Outros', false, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Vendas', false, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Serviços', false, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Aluguel', false, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Utilities', false, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Salários', false, wid_loja, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

    INSERT INTO "Category" ("id", "name", "isFavorite", "walletId", "createdAt", "updatedAt") VALUES
      (gen_random_uuid()::text, 'Mercado', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Transporte', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Lazer', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Contas', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Saúde', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Pet', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Presentes', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Vestuário', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
      (gen_random_uuid()::text, 'Outros', true, wid_pessoal, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
  END LOOP;
END $$;

-- Backfill: match legacy category string to Loja category name, else Outros
UPDATE "Transaction" t
SET "categoryId" = m."categoryId"
FROM (
  SELECT
    t2.id AS tid,
    COALESCE(
      (
        SELECT c.id
        FROM "Category" c
        INNER JOIN "Wallet" w ON c."walletId" = w.id
        WHERE w."organizationId" = t2."organizationId"
          AND w.name = 'Loja'
          AND c.name = NULLIF(TRIM(t2.category), '')
        LIMIT 1
      ),
      (
        SELECT c.id
        FROM "Category" c
        INNER JOIN "Wallet" w ON c."walletId" = w.id
        WHERE w."organizationId" = t2."organizationId"
          AND w.name = 'Loja'
          AND c.name = 'Outros'
        LIMIT 1
      )
    ) AS "categoryId"
  FROM "Transaction" t2
) m
WHERE t.id = m.tid AND m."categoryId" IS NOT NULL;

-- Drop legacy column
ALTER TABLE "Transaction" DROP COLUMN "category";

-- Require category
ALTER TABLE "Transaction" ALTER COLUMN "categoryId" SET NOT NULL;

-- AddForeignKey
ALTER TABLE "Transaction" ADD CONSTRAINT "Transaction_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "Category"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- CreateIndex
CREATE INDEX "Transaction_categoryId_idx" ON "Transaction"("categoryId");
