import { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Box, Button, TextField, Typography } from '@mui/material'

type SplitPaymentStepProps = {
  totalRemainingPrice: number
  amountOfPeople: number
  setStep: (step: number) => void
  setTotalRemainingPrice: (price: number) => void
}

const SplitPaymentStep: FC<SplitPaymentStepProps> = ({
  totalRemainingPrice,
  amountOfPeople,
  setStep,
  setTotalRemainingPrice,
}) => {
  const { t } = useTranslation()
  const [peopleCount, setPeopleCount] = useState<number>(amountOfPeople)

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
        position: 'relative', // To position the back button
      }}
    >
      <Button
        variant="contained"
        color="secondary"
        onClick={setStep.bind(null, 1)}
        sx={{
          position: 'absolute',
          top: 16,
          left: 16,
        }}
      >
        {t('payment.itemSelectionStep.back')}
      </Button>
      <Typography
        variant="h3"
        component="h1"
        sx={{
          marginBottom: 2,
          textAlign: 'center',
        }}
      >
        {t('payment.splitPaymentStep.remainingPrice')}
      </Typography>
      <Typography
        variant="h4"
        component="p"
        sx={{
          marginBottom: 4, // Increase the margin to add more space
          textAlign: 'center',
          fontWeight: 'bold',
        }}
      >
        {totalRemainingPrice.toFixed(2)}
      </Typography>
      <TextField
        label={t('payment.splitPaymentStep.numberOfPeople')}
        type="number"
        value={peopleCount}
        onChange={(e) => setPeopleCount(Number(e.target.value))}
        sx={{
          marginBottom: 2,
          width: '300px', // Set a specific width
        }}
        InputProps={{
          style: {
            fontSize: '1.25rem', // Set the font size for the input text
          },
        }}
        InputLabelProps={{
          style: {
            fontSize: '1.25rem', // Set the font size for the label text
          },
        }}
      />
      <Typography
        variant="h4"
        component="p"
        sx={{
          marginBottom: 2,
          textAlign: 'center',
        }}
      >
        {t('payment.splitPaymentStep.pricePerPerson')}
      </Typography>
      <Typography
        variant="h5"
        component="p"
        sx={{
          marginBottom: 2,
          textAlign: 'center',
          fontWeight: 'bold',
        }}
      >
        {peopleCount === 0
          ? '-'
          : (totalRemainingPrice / peopleCount).toFixed(2)}
      </Typography>
      <Button
        variant="contained"
        color="primary"
        onClick={() => {
          setStep(2)
          setTotalRemainingPrice(0)
        }}
        sx={{
          margin: '2px 0',
          width: 'fit-content',
          padding: '16px 32px', // Make the button bigger
          fontSize: '1.25rem', // Increase font size
        }}
      >
        {t('payment.itemSelectionStep.pay')}
      </Button>
    </Box>
  )
}

export default SplitPaymentStep
