/**
 * Intervalo inclusive de createdAt para filtros de período (calendário local).
 */
export function createdAtRangeForPeriod(
  period: string | null | undefined,
): { gte: Date; lte: Date } | null {
  if (period !== "this-month" && period !== "last-month") return null;

  const now = new Date();

  if (period === "this-month") {
    const gte = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0, 0);
    const lte = new Date(
      now.getFullYear(),
      now.getMonth() + 1,
      0,
      23,
      59,
      59,
      999,
    );
    return { gte, lte };
  }

  const gte = new Date(now.getFullYear(), now.getMonth() - 1, 1, 0, 0, 0, 0);
  const lte = new Date(
    now.getFullYear(),
    now.getMonth(),
    0,
    23,
    59,
    59,
    999,
  );
  return { gte, lte };
}
