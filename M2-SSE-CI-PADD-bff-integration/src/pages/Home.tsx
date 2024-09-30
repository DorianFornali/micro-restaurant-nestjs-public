import { useTranslation } from 'react-i18next'
import { Box, Button } from '@mui/material'
import { useState } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { routes } from '../router/definitions'

const HomePage = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [started, setStarted] = useState(false)

  const handleStart = () => {
    setStarted(true)
  }

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        height: '100vh',
      }}
    >
      {/* Ad image' */}
      <Box
        component="img"
        src="/img/fake-ad.jpg"
        alt="ad"
        sx={{
          width: '100%',
          height: 'auto',
          maxHeight: '10vh',
          objectFit: 'cover',
        }}
      />

      {!started ? (
        <>
          {/* Restaurant image */}
          <Box
            component="img"
            src="/img/restaurant.jpg"
            alt="restaurant"
            sx={{
              width: '100%',
              height: 'auto',
              paddingY: 2,
            }}
          />

          {/* Start order button */}
          <Button
            variant="contained"
            color="primary"
            size="large"
            onClick={handleStart}
            sx={{
              width: '100%',
              height: '100vh',
              padding: 1,
            }}
          >
            {t('home.start')}
          </Button>
        </>
      ) : (
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'row',
            gap: 2,
            flexGrow: 1,
            justifyContent: 'center',
            alignItems: 'center',
            paddingX: 1,
          }}
        >
          <Button
            variant="contained"
            color="primary"
            size="large"
            sx={{
              width: '40vw',
              height: '15vh',
            }}
            onClick={() =>
              navigate({
                to: routes.command.path,
              })
            }
          >
            {t('home.startOrder')}
          </Button>

          <Button
            variant="contained"
            color="primary"
            size="large"
            sx={{
              width: '40vw',
              height: '15vh',
            }}
            onClick={() =>
              navigate({
                to: routes.payment.path,
              })
            }
          >
            {t('home.startPayment')}
          </Button>
        </Box>
      )}
    </Box>
  )
}

export default HomePage
