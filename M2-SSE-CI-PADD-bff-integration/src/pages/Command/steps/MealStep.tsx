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
import { MenuItemCategoryAll, Menu, MenuItem } from '../../../types/Menu'

import APP from '../../../config/api'


const MealStep: FC = () => {
  const { t } = useTranslation()
  const navigate = useNavigate()
  const [step, setStep] = useState(useAppStore.getState().mealStep)
  const tableNumber = useAppStore((state) => state.tableNumber);
  const menu = useAppStore((state) => state.menu)
  const selectedMeals = useAppStore((state) => state.selectedMeal)
  const numberOfPeople = useAppStore((state) => state.peopleCount)

  console.log('menu', menu)

  const spliceSelectedMeal = useMemo(
    () => getViewOfSelectedMeals(selectedMeals, 4),
    [selectedMeals]
  )

  const openPanier = () => {
    navigate({
      to: routes.panier.path,
    })
  }


  const retrieveMenuItems = (
    selectedMeals: { [key: string]: string[] },
    menu: Menu
  ): { menuItem: MenuItem }[] => {
    const menuItems: { menuItem: MenuItem }[] = [];
  
    // Loop through each category in selectedMeals
    Object.keys(selectedMeals).forEach((category) => {
      // Make sure the category is one of the values in MenuItemCategoryAll before proceeding
      if (Object.values(MenuItemCategoryAll).includes(category as MenuItemCategoryAll)) {
        selectedMeals[category].forEach((mealId) => {
          // Find the full meal details in the menu by mealId
          const meal = Object.values(menu)
            .flat()
            .find((item) => item._id === mealId);
  
          // If the meal is found, push it to the menuItems array with the correct structure
          if (meal) {
            menuItems.push({
              menuItem: {
                _id: meal._id,
                fullName: meal.fullName,
                shortName: meal.shortName,
                price: meal.price,
                category: meal.category,
                image: meal.image,
                supplement: category === MenuItemCategoryAll.ADDON, // Set supplement to true if category is ADDON
              },
            });
          }
        });
      }
    });
  
    return menuItems;
  };
  
  

  const validateCommand = () => {
    // Prepare the data to send to the API
    const menuItems = retrieveMenuItems(selectedMeals, menu);
    console.log(menuItems);
    const selectedMealsData = {
      tableNumber: tableNumber, // Table number from the global store
      menuItems: menuItems,      // Selected meals from the global store
    };
    // Make the API call to submit the order
    fetch(`${APP.API_POST_NEW_ORDER}`, {
      method: 'POST', // Assuming POST request
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(selectedMealsData), // Sending selected meals data as JSON
      
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to submit the order'); // Handle non-200 responses
        }
        return response.text(); // Parse the response
      })
      .then((data) => {
        console.log('Order submitted successfully', data); // Handle success response
  
        // After successful API call, navigate to the final page
        navigate({
          to: routes.final.path,
        });
      })
      .catch((error) => {
        console.error('Error submitting order:', error); // Handle errors
      });
  };
  

  const handleStep = (step: number) => {
    if (step === Object.keys(MenuItemCategoryAll).length) {
      return validateCommand()
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
        price={spliceSelectedMeal
          .map((mealId) => {
            const meal = Object.values(menu)
              .flat()
              .find((meal) => meal._id === mealId)
            return meal?.price || 0
          })
          .reduce((acc, price) => acc + price, 0)}
        primaryBtnProps={{
          label: t('command.mealStep.primary'),
          onClick: validateCommand,
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
