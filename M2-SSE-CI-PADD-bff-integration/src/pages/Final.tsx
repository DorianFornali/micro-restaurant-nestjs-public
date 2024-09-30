import { useTranslation } from 'react-i18next'
import { Box, Button, Typography } from '@mui/material'
import { useNavigate } from '@tanstack/react-router'
import { routes } from '../router/definitions'
import { ArrowRight } from '@mui/icons-material'
import { useEffect } from 'react'
import { useAppStore } from '../store/store'

const FinalPage = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()

  useEffect(() => {
    useAppStore.getState().reset()
  }, [])

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        height: '100vh',
      }}
    >
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          height: '100%',
        }}
      >
        <Typography variant="h1" component="h1" gutterBottom align="center">
          {t('final.title')}
        </Typography>
        <Button
          onClick={() =>
            navigate({
              to: routes.root.path,
            })
          }
          sx={{ marginTop: 16 }}
          endIcon={<ArrowRight />}
        >
          {t('final.back')}
        </Button>
      </Box>
    </Box>
  )
}

export default FinalPage
