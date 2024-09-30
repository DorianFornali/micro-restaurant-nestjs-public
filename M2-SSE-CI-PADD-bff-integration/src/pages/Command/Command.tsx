import { useState } from 'react'
import Box from '@mui/material/Box'
import { useTranslation } from 'react-i18next'
import BasicStep from '../common/BasicStep'
import MealStep from './steps/MealStep'
import { useAppStore } from '../../store/store'

const CommandPage = () => {
  const { t } = useTranslation()
  const [step, setStep] = useState(useAppStore.getState().step || 0)
  const [tableNumber, setTableNumber] = useState(0)
  const [peopleCount, setPeopleCount] = useState(0)

  const handleStep = (step: number) => {
    setStep(step)
    useAppStore.getState().setStep(step)
  }

  const handleTableNumber = (tableNumber: number) => {
    setTableNumber(tableNumber)
    useAppStore.getState().setTableNumber(tableNumber)
  }

  const handlePeopleCount = (peopleCount: number) => {
    setPeopleCount(peopleCount)
    useAppStore.getState().setPeopleCount(peopleCount)
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
          title={t('command.tableStep.title')}
          inputProps={{
            id: 'table-number',
            label: t('command.tableStep.label'),
            placeholder: t('command.tableStep.placeholder'),
            value: tableNumber,
            onChange: (e) => handleTableNumber(Number(e.target.value)),
          }}
          submitBtnProps={{
            label: t('command.tableStep.next'),
            onClick: () => handleStep(1),
          }}
        />
      )}
      {step === 1 && (
        <BasicStep
          title={t('command.peopleStep.title')}
          inputProps={{
            id: 'people-count',
            label: t('command.peopleStep.label'),
            placeholder: t('command.peopleStep.placeholder'),
            value: peopleCount,
            onChange: (e) => handlePeopleCount(Number(e.target.value)),
          }}
          submitBtnProps={{
            label: t('command.peopleStep.next'),
            onClick: () => handleStep(2),
          }}
        />
      )}
      {step === 2 && <MealStep />}
    </Box>
  )
}

export default CommandPage
