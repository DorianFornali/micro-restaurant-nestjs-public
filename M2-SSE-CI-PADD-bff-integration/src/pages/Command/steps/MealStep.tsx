import { FC, useMemo, useState } from 'react'
import { useNavigate } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'
import { routes } from '../../../router/definitions'
import {
  Box,
  Button,
  Step,
  StepLabel,
  Stepper,
  Typography,
} from '@mui/material'
import MealCard from '../../../components/MealCard'
import CommandCard from '../../../components/CommandCard'
import { useAppStore } from '../../../store/store'
import {
  getCurrentStep,
  getCurrentStepTranslation,
  getViewOfSelectedMeals,
} from '../../../utils/Menu'
import { MenuItemCategoryAll } from '../../../types/Menu'

import useCommand from '../../../hooks/useCommand'


const MealStep: FC = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [step, setStep] = useState(useAppStore.getState().mealStep)
  const tableNumber = useAppStore((state) => state.tableNumber);
  const menu = useAppStore((state) => state.menu)
  const selectedMeals = useAppStore((state) => state.selectedMeal)
  const numberOfPeople = useAppStore((state) => state.peopleCount)
  const { validateCommand } = useCommand()

  console.log('menu', menu)

  const spliceSelectedMeal = useMemo(
    () => getViewOfSelectedMeals(selectedMeals, 4),
    [selectedMeals]
  )

  const flattenMenu = useMemo(() => Object.values(menu).flat(), [menu])

  const flattenSelectedMeals = useMemo(
    () => Object.values(selectedMeals).flat(),
    [selectedMeals]
  )
  const openPanier = () => {
    navigate({
      to: routes.panier.path,
    })
  }



  const handleStep = (step: number) => {
    if (step === Object.keys(MenuItemCategoryAll).length) {
      return validateCommand(selectedMeals, menu, tableNumber)
    }
    console.log('step', step);
    setStep(step)
    useAppStore.getState().setMealStep(step)
  }

  const handleSelectedMeal = (mealId: string, numberOfItem: number) => {
    const category = getCurrentStep(step)
    const categorySelectedMeals = selectedMeals[category]
    const mealIndex = categorySelectedMeals.indexOf(mealId)
    if (mealIndex === -1) {
      if (categorySelectedMeals.length <= numberOfPeople) {
        useAppStore.getState().setSelectedMeal({
          ...selectedMeals,
          [category]: [
            ...categorySelectedMeals,
            ...Array(numberOfItem).fill(mealId),
          ],
        })
      }
    } else {
      useAppStore.getState().setSelectedMeal({
        ...selectedMeals,
        [category]: [
          ...categorySelectedMeals.slice(0, mealIndex),
          ...categorySelectedMeals.slice(mealIndex + 1),
        ],
      })
    }
  }

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
      <Stepper
        activeStep={step}
        alternativeLabel
        sx={{
          marginBottom: 2,
        }}
      >
        {Object.keys(MenuItemCategoryAll).map((label) => (
          <Step key={label}>
            <StepLabel>{getCurrentStepTranslation(label, t)}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Button
        variant="contained"
        color="secondary"
        disabled={step === 0}
        onClick={() => handleStep(step - 1)}
        sx={{
          margin: '2px 0',
          width: 'fit-content',
        }}
      >
        {t('command.mealStep.back')}
      </Button>
      <Typography
        variant="h3"
        component="h1"
        sx={{
          marginBottom: 2,
        }}
      >
        {getCurrentStepTranslation(step, t)}
      </Typography>
      <Box
        sx={{
          display: 'grid',
          gridTemplateColumns: 'repeat(2, 1fr)',
          gap: 2,
          maxHeight: 'calc(100% - 100px)',
          overflow: 'auto',
        }}
      >
        {menu[getCurrentStep(step)].map((meal) => (
          <MealCard
            key={meal._id}
            id={meal._id}
            img={meal.image}
            name={meal.shortName}
            price={meal.price}
            isSelected={selectedMeals[getCurrentStep(step)].includes(meal._id)}
            openModalOnSelect={true}
            onSelect={(numberOfItem = 1) =>
              handleSelectedMeal(meal._id, numberOfItem)
            }
            onClick={() => {
              navigate({
                to: routes.mealDetail.path,
                params: { mealId: meal._id },
              })
            }}
          />
        ))}
      </Box>
      <Box component="div" sx={{ flexGrow: 1 }} />
      <Button
        variant="contained"
        color="primary"
        onClick={() => {
          handleStep(step + 1)
        }}
        disabled={selectedMeals[getCurrentStep(step)].length === 0}
        sx={{
          margin: '8px 0',
        }}
      >
        {t('command.mealStep.next')}
      </Button>

      <CommandCard
        items={spliceSelectedMeal.map((mealId) => {
          const meal = Object.values(menu)
            .flat()
            .find((meal) => meal._id === mealId)
          return {
            name: meal?.shortName || '',
            img: meal?.image || '',
          }
        })}
        price={flattenSelectedMeals
          .map((mealId: string) => {
            const meal = flattenMenu.find((meal) => meal._id === mealId)
            return meal?.price || 0
          })
          .reduce((acc, price) => acc + price, 0)}
        primaryBtnProps={{
          label: t('command.mealStep.primary'),
          onClick: () => validateCommand(selectedMeals, menu, tableNumber),
        }}
        secondaryBtnProps={{
          label: t('command.mealStep.secondary'),
          onClick: openPanier,
        }}
      />
    </Box>
  )
}

export default MealStep
