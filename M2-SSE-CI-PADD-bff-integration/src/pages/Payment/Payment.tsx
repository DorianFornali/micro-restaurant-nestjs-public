import APP from '../../config/api'
import { useState, useEffect } from 'react'
import Box from '@mui/material/Box'
import BasicStep from '../common/BasicStep'
import ItemSelectionStep from './steps/ItemSelectionStep'
import PaymentStep from './steps/PaymentStep'
import SplitPaymentStep from './steps/SplitPaymentStep'
import { useTranslation } from 'react-i18next'
import { useNavigate } from '@tanstack/react-router'
import { routes } from '../../router/definitions'


const PaymentPage = () => {
  const { t } = useTranslation()
  const [step, setStep] = useState(0)
  const [tableNumber, setTableNumber] = useState(0)
  const [peopleCount] = useState(0)
  const [totalRemainingPrice, setTotalRemainingPrice] = useState(0)
  const navigate = useNavigate()

  const [supplementItems, setSupplementItems] = useState([]) // State to store supplements
  const [error, setError] = useState(null) // Error state
  const [loading, setLoading] = useState(false) // Loading state
 
  
  const handleNext = () => {
    if (tableNumber > 0) {
      // Fetch supplements only when the "Next" button is clicked and tableNumber is valid
      setLoading(true)
      fetch(`${APP.API_SUPPLEMENTS}/${tableNumber}`)
        .then((response) => {
          if (!response.ok) {
            console.log(response)
            throw new Error('Failed to fetch supplements')
          }
          return response.json()
        })
        .then((data) => {
          setSupplementItems(
            data.map((item: any) => ({
              id: item._id,
              img: item.image,
              name: item.fullName,
              price: item.price,
              onClick: () => console.log(`${item.fullName} clicked`),
              onSelect: () => console.log(`${item.fullName} selected`),
            }))
          )
          setTotalRemainingPrice(
            data.reduce((acc: number, item: { price: number }) => acc + item.price, 0)
          );
          // Move to the next step only after fetching supplements successfully
          setStep(1)
        })
        .catch((err) => {
          console.error(err)
          setError(err.message)
        })
        .finally(() => setLoading(false))
    }
  }

  const handleBack = () => {
    if (totalRemainingPrice <= 0) {
      navigate({
        to: routes.final.path,
      })
    }
    setStep((prevStep) => Math.max(prevStep - 1, 0))
  }

  const handleUpdateTotalRemainingPrice = (price: number) => {
    setTotalRemainingPrice(price)
  }

  return (
    <Box
      component="section"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        height: '100vh',
      }}
    >
      {step === 0 && (
        <BasicStep
          title={t('command.tableStep.titlePayment')}
          inputProps={{
            id: 'table-number',
            label: t('command.tableStep.label'),
            placeholder: t('command.tableStep.placeholder'),
            value: tableNumber,
            onChange: (e) => setTableNumber(Number(e.target.value)),
          }}
          submitBtnProps={{
            label: t('command.tableStep.next'),
            onClick: () => handleNext(),
          }}
        />
      )}
      {step === 1 && totalRemainingPrice <= 0 && (
        <BasicStep
          title={t('command.tableStep.titlePayment')}
          inputProps={{
            id: 'table-number',
            label: t('command.tableStep.label'),
            placeholder: t('command.tableStep.placeholder'),
            value: tableNumber,
            onChange: (e) => setTableNumber(Number(e.target.value)),
          }}
          submitBtnProps={{
            label: t('command.tableStep.next'),
            onClick: () => setStep(1),
          }}
        />
      )}
      {step === 1 && totalRemainingPrice > 0 && (
        <ItemSelectionStep
          numberOfPeople={peopleCount}
          supplementItems={supplementItems}
          onBack={handleBack}
          onRemainingPriceUpdate={handleUpdateTotalRemainingPrice}
          remainingPrice={totalRemainingPrice}
          setStep={setStep}
        />
      )}
      {step === 2 && <PaymentStep onBackToSelection={handleBack} />}

      {step === 3 && (
        <SplitPaymentStep
          totalRemainingPrice={totalRemainingPrice}
          amountOfPeople={peopleCount}
          setStep={setStep}
          setTotalRemainingPrice={setTotalRemainingPrice}
        />
      )}
    </Box>
  )
}

export default PaymentPage
