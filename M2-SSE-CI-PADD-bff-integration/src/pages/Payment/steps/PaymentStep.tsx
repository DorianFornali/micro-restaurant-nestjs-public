// This component contains:
// a simple text saying Merci pour le paiement
// and a spinner

import { Box, CircularProgress, Typography } from '@mui/material'
import { FC, useEffect } from 'react'
import { useTranslation } from 'react-i18next'

type PaymentStepProps = {
  onBackToSelection: () => void
}

const PaymentStep: FC<PaymentStepProps> = ({ onBackToSelection }) => {
  const { t } = useTranslation()

  useEffect(() => {
    const timer = setTimeout(() => {
      onBackToSelection()
    }, 2000)

    return () => clearTimeout(timer) // Cleanup the timer if the component unmounts
  }, [onBackToSelection])

  return (
    <Box
      component="div"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        padding: 2,
        alignItems: 'center', // Center horizontally
        justifyContent: 'center', // Center vertically
      }}
    >
      <Typography
        variant="h3"
        component="h1"
        sx={{
          marginBottom: 2,
        }}
      >
        {t('payment.paymentStep.paymentInProgress')}
      </Typography>
      <CircularProgress />
    </Box>
  )
}

export default PaymentStep
