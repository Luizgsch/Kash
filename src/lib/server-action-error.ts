/**
 * Erros de `redirect()` do Next.js devem ser relançados; demais falhas viram resposta segura ao cliente.
 */
export function isNextRedirectError(error: unknown): boolean {
  return (
    typeof error === "object" &&
    error !== null &&
    "digest" in error &&
    typeof (error as { digest?: unknown }).digest === "string" &&
    String((error as { digest: string }).digest).startsWith("NEXT_REDIRECT")
  );
}

export const SERVER_ACTION_UNAVAILABLE_PT =
  "O servidor demorou a responder ou não está disponível. Tenta outra vez dentro de instantes.";

export function logServerActionError(context: string, error: unknown) {
  console.error(`[server action: ${context}]`, error);
}
