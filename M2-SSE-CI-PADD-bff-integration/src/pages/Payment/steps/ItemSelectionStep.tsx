// This component contains:
// A button to go back
// A title
// A list of the menu items as supplements
// A button to go to the payment page
// A display of the total price remaining to pay

import { FC, useState } from 'react'
import { useTranslation } from 'react-i18next'
import { Box, Button, Typography } from '@mui/material'
import MealCard, { MealCardProps } from '../../../components/MealCard'

type ItemSelectionStepProps = {
  numberOfPeople: number // determine the max number of items we can select
  supplementItems: MealCardProps[] // dynamically provided supplement items
  onBack: () => void // back button handler
  onRemainingPriceUpdate: (price: number) => void
  remainingPrice: number
  setStep: (step: number) => void
}

const ItemSelectionStep: FC<ItemSelectionStepProps> = ({
  supplementItems,
  onBack,
  onRemainingPriceUpdate,
  remainingPrice,
  setStep,
}) => {
  const { t } = useTranslation()
  const [selectedItems, setSelectedItems] = useState<string[]>([])

  const totalPriceSelected = selectedItems.reduce((acc, itemId) => {
    const item = supplementItems.find((item) => item.id === itemId)
    return acc + (item?.price || 0)
  }, 0)

  return (
    <Box
      component="div"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        height: '100%',
        padding: 2,
      }}
    >
      <Button
        variant="contained"
        color="secondary"
        onClick={() => {
          // Handle back button click
          onBack()
        }}
        sx={{
          margin: '2px 0',
          width: 'fit-content',
        }}
      >
        {t('payment.itemSelectionStep.back')}
      </Button>
      <Typography
        variant="h3"
        component="h1"
        sx={{
          marginBottom: 2,
        }}
      >
        {t('payment.itemSelectionStep.title')}
      </Typography>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: 'repeat(2, 1fr)',
          gap: 2,
          maxHeight: 'calc(100% - 200px)', // Adjusted to make space for the bottom section
          overflow: 'auto',
        }}
      >
        {supplementItems.map((item) => (
          <MealCard
            key={item.id}
            isSelected={selectedItems.includes(item.id)}
            {...item}
            onSelect={() => {
              setSelectedItems((prev) => {
                if (prev.includes(item.id)) {
                  return prev.filter((id) => id !== item.id)
                }
                return [...prev, item.id]
              })
            }}
          />
        ))}
      </Box>
      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          padding: 2,
          borderTop: '1px solid #ccc',
          marginTop: 'auto', // Ensure this section is at the bottom
        }}
      >
        <Box
          sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 2,
            borderRight: '1px solid #ccc',
          }}
        >
          <Typography variant="h6">
            {t('payment.itemSelectionStep.totalPriceSelected')}
          </Typography>
          <Typography variant="h4" sx={{ marginTop: 1 }}>
            {totalPriceSelected} €
          </Typography>
          <Button
            variant="contained"
            color="primary"
            sx={{ marginTop: 2 }}
            onClick={() => {
              setStep(2)
              onRemainingPriceUpdate(remainingPrice - totalPriceSelected)
            }}
          >
            {t('payment.itemSelectionStep.pay')}
          </Button>
        </Box>
        <Box
          sx={{
            flex: 1,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
            padding: 2,
          }}
        >
          <Typography variant="h6">
            {t('payment.itemSelectionStep.remainingTotal')}
          </Typography>
          <Typography variant="h4" sx={{ marginTop: 1 }}>
            {remainingPrice} €
          </Typography>
          <Button
            variant="contained"
            color="secondary"
            sx={{ marginTop: 2 }}
            onClick={() => {
              setStep(3)
            }}
          >
            {t('payment.itemSelectionStep.splitBill')}
          </Button>
          <Button
            variant="contained"
            color="primary"
            sx={{ marginTop: 2 }}
            onClick={() => {
              onRemainingPriceUpdate(0)
              setStep(2)
            }}
          >
            {t('payment.itemSelectionStep.payForEverything')}
          </Button>
        </Box>
      </Box>
    </Box>
  )
}

export default ItemSelectionStep
