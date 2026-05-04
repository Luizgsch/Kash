import { createTheme } from '@mui/material/styles';

const kashTheme = createTheme({
  palette: {
    mode: 'dark',
    background: {
      default: '#0D0D0D',
      paper: '#1A1A1A',
    },
    primary: {
      main: '#C4A882',
      light: '#E0C5A8',
      dark: '#8B7A5C',
      contrastText: '#0D0D0D',
    },
    secondary: {
      main: '#8A8A8A',
      light: '#A8A8A8',
      dark: '#6A6A6A',
      contrastText: '#E8E8E8',
    },
    error: {
      main: '#EF5350',
      light: '#F44336',
      dark: '#D32F2F',
    },
    success: {
      main: '#4CAF50',
      light: '#66BB6A',
      dark: '#388E3C',
    },
    warning: {
      main: '#FB8C00',
    },
    info: {
      main: '#1E90FF',
    },
    divider: '#2E2E2E',
    text: {
      primary: '#E8E8E8',
      secondary: '#8A8A8A',
      disabled: '#4A4A4A',
    },
  },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontSize: '2rem', fontWeight: 500 },
    h2: { fontSize: '1.75rem', fontWeight: 500 },
    h3: { fontSize: '1.5rem', fontWeight: 500 },
    h4: { fontSize: '1.25rem', fontWeight: 500 },
    h5: { fontSize: '1.125rem', fontWeight: 500 },
    h6: { fontSize: '1rem', fontWeight: 500 },
    body1: { fontSize: '1rem' },
    body2: { fontSize: '0.875rem' },
    button: { textTransform: 'none', fontWeight: 500 },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          borderRadius: '12px',
          textTransform: 'none',
          fontWeight: 500,
        },
        contained: {
          backgroundColor: '#C4A882',
          color: '#0D0D0D',
          '&:hover': {
            backgroundColor: '#E0C5A8',
          },
        },
        outlined: {
          borderColor: '#2E2E2E',
          color: '#E8E8E8',
          '&:hover': {
            borderColor: '#C4A882',
            backgroundColor: 'rgba(196, 168, 130, 0.08)',
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: '10px',
            backgroundColor: '#1A1A1A',
            '& fieldset': {
              borderColor: '#2E2E2E',
            },
            '&:hover fieldset': {
              borderColor: '#C4A882',
            },
            '&.Mui-focused fieldset': {
              borderColor: '#C4A882',
            },
          },
        },
      },
    },
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundColor: '#1A1A1A',
          borderColor: '#2E2E2E',
        },
      },
    },
    MuiBottomNavigation: {
      styleOverrides: {
        root: {
          backgroundColor: '#1A1A1A',
          borderTop: '1px solid #2E2E2E',
        },
      },
    },
    MuiBottomNavigationAction: {
      styleOverrides: {
        root: {
          color: '#4A4A4A',
          '&.Mui-selected': {
            color: '#C4A882',
          },
        },
      },
    },
  },
});

export default kashTheme;
