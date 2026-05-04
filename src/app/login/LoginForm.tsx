"use client";

import { useState } from "react";
import { signIn } from "next-auth/react";
import Link from "next/link";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  Stack,
  CircularProgress,
  InputAdornment,
} from "@mui/material";
import { Mail, Lock } from "lucide-react";

type LoginFormProps = {
  canEmailLogin: boolean;
};

export function LoginForm({ canEmailLogin }: LoginFormProps) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [sending, setSending] = useState(false);
  const [credentialsPending, setCredentialsPending] = useState(false);
  const [googlePending, setGooglePending] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  async function handleGoogleSignIn() {
    setMessage(null);
    setGooglePending(true);
    try {
      const r = await signIn("google", { callbackUrl: "/", redirect: false });
      if (r && typeof r === "object" && "ok" in r) {
        if (!r.ok || r.error) {
          setMessage(
            r.error === "Configuration"
              ? "Google não está configurado: defina GOOGLE_CLIENT_ID e GOOGLE_CLIENT_SECRET (ou AUTH_GOOGLE_ID e AUTH_GOOGLE_SECRET)."
              : "Não foi possível iniciar o login com Google. Tente de novo."
          );
          setGooglePending(false);
          return;
        }
        if ("url" in r && typeof r.url === "string" && r.url) {
          window.location.href = r.url;
          return;
        }
      }
      setMessage("Não foi possível iniciar o login com Google.");
    } catch {
      setMessage("Não foi possível iniciar o login com Google.");
    }
    setGooglePending(false);
  }

  async function handleCredentials(e: React.FormEvent) {
    e.preventDefault();
    if (!email.trim() || !password) return;
    setCredentialsPending(true);
    setMessage(null);
    const r = await signIn("credentials", {
      email: email.trim().toLowerCase(),
      password,
      callbackUrl: "/",
      redirect: false,
    });
    setCredentialsPending(false);
    const err = r && typeof r === "object" && "error" in r ? r.error : null;
    if (err) {
      setMessage("E-mail ou senha incorretos.");
      return;
    }
    if (r && typeof r === "object" && "url" in r && typeof r.url === "string") {
      window.location.href = r.url;
    }
  }

  async function handleEmail(e: React.FormEvent) {
    e.preventDefault();
    if (!canEmailLogin || !email.trim()) return;
    setSending(true);
    setMessage(null);
    const r = await signIn("resend", {
      email: email.trim(),
      callbackUrl: "/",
      redirect: false,
    });
    setSending(false);
    const err = r && typeof r === "object" && "error" in r ? r.error : null;
    if (err) {
      setMessage("Não foi possível enviar o link. Tente de novo.");
    } else {
      setMessage("Abra o link enviado ao seu e-mail para concluir o acesso.");
    }
  }

  return (
    <Box sx={{ maxWidth: "sm", mx: "auto", width: "100%" }}>
      <Stack spacing={3}>
        <Button
          fullWidth
          variant="outlined"
          size="large"
          onClick={handleGoogleSignIn}
          disabled={googlePending}
          startIcon={googlePending ? <CircularProgress size={20} /> : <GoogleGlyph />}
        >
          {googlePending ? "Abrindo Google…" : "Continuar com Google"}
        </Button>

        <Box component="form" onSubmit={handleCredentials} sx={{ display: "flex", flexDirection: "column", gap: 2 }}>
          <Typography variant="caption" align="center" sx={{ color: "text.secondary" }}>
            ou com e-mail e senha
          </Typography>

          <TextField
            fullWidth
            label="E-mail"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="seu@email.com"
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <Mail size={18} />
                  </InputAdornment>
                ),
              },
            }}
          />

          <TextField
            fullWidth
            label="Senha"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            slotProps={{
              input: {
                startAdornment: (
                  <InputAdornment position="start">
                    <Lock size={18} />
                  </InputAdornment>
                ),
              },
            }}
          />

          <Button
            fullWidth
            variant="contained"
            size="large"
            type="submit"
            disabled={credentialsPending}
          >
            {credentialsPending ? "Entrando…" : "Entrar com senha"}
          </Button>
        </Box>

        <Typography variant="body2" align="center" sx={{ color: "text.secondary" }}>
          Novo por aqui?{" "}
          <Link href="/auth/register" style={{ color: "inherit", textDecoration: "none" }}>
            <Box component="span" sx={{ color: "primary.main", "&:hover": { textDecoration: "underline" } }}>
              Criar conta
            </Box>
          </Link>
        </Typography>

        {canEmailLogin && (
          <Box component="form" onSubmit={handleEmail} sx={{ display: "flex", gap: 1, alignItems: "flex-end" }}>
            <Typography variant="caption" sx={{ color: "text.secondary", mb: 1 }}>
              ou envie um link
            </Typography>
            <TextField
              label="E-mail"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              size="small"
              sx={{ flex: 1 }}
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <Mail size={18} />
                    </InputAdornment>
                  ),
                },
              }}
            />
            <Button variant="contained" type="submit" disabled={sending}>
              {sending ? "Enviando…" : "Link"}
            </Button>
          </Box>
        )}

        {message && <Alert severity={message.includes("sucesso") ? "success" : "info"}>{message}</Alert>}
      </Stack>
    </Box>
  );
}

function GoogleGlyph() {
  return (
    <svg width="20" height="20" viewBox="0 0 48 48" aria-hidden>
      <path
        fill="#EA4335"
        d="M24 9.5c3.54 0 6.71 1.22 9.21 3.6l6.85-6.85C35.9 2.38 30.47 0 24 0 14.62 0 6.51 5.38 2.56 13.22l7.98 6.19C12.43 13.72 17.74 9.5 24 9.5z"
      />
      <path
        fill="#4285F4"
        d="M46.98 24.55c0-1.57-.15-3.09-.38-4.55H24v9.02h12.94c-.58 2.96-2.26 5.48-4.78 7.18l7.73 6c4.51-4.18 7.09-10.36 7.09-17.65z"
      />
      <path
        fill="#FBBC05"
        d="M10.53 28.59c-.48-1.45-.76-2.99-.76-4.59s.27-3.14.76-4.59l-7.98-6.19C.92 16.46 0 20.12 0 24c0 3.88.92 7.54 2.56 10.78l7.97-6.19z"
      />
      <path
        fill="#34A853"
        d="M24 48c6.48 0 11.93-2.13 15.89-5.81l-7.73-6c-2.15 1.45-4.92 2.3-8.16 2.3-6.26 0-11.57-4.22-13.47-9.91l-7.98 6.2C6.51 42.63 14.62 48 24 48z"
      />
    </svg>
  );
}
