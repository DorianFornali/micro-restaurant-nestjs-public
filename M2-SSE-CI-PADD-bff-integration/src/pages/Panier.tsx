import { FC, useMemo } from 'react'
import { Box, Typography } from '@mui/material'
import { useNavigate } from '@tanstack/react-router'
import { useTranslation } from 'react-i18next'
import MealCard from '../components/MealCard'
import CommandCard from '../components/CommandCard'
import { routes } from '../router/definitions'
import { useAppStore } from '../store/store'
import { getViewOfSelectedMeals } from '../utils/Menu'
import { MenuItemCategoryAll } from '../types/Menu'
import useCommand from '../hooks/useCommand'


const Panier: FC = () => {
  const { t } = useTranslation()
  const menu = useAppStore((state) => state.menu)
  const tableNumber = useAppStore((state) => state.tableNumber)
  const selectedMeals = useAppStore((state) => state.selectedMeal)
  const setSelectedMeal = useAppStore((state) => state.setSelectedMeal)
  const { validateCommand } = useCommand()
  const navigate = useNavigate()

  const spliceSelectedMeal = useMemo(
    () => getViewOfSelectedMeals(selectedMeals, 4),
    [selectedMeals]
  )

  const flattenMenu = useMemo(() => Object.values(menu).flat(), [menu])

  const flattenSelectedMeals = useMemo(
    () => Object.values(selectedMeals).flat(),
    [selectedMeals]
  )

  const deleteItem = (id: string) => {
    const updatedSelectedMeals = { ...selectedMeals }

    // We start removing in the ADDON
    const searchOrder = [
      MenuItemCategoryAll.ADDON,
      MenuItemCategoryAll.STARTER,
      MenuItemCategoryAll.MAIN,
      MenuItemCategoryAll.DESSERT,
      MenuItemCategoryAll.BEVERAGE,
    ]

    for (const category of searchOrder) {
      if (updatedSelectedMeals[category]) {
        const index = updatedSelectedMeals[category].findIndex(
          (item) => item === id
        )

        if (index !== -1) {
          updatedSelectedMeals[category].splice(index, 1)
          break
        }
      }
    }

    setSelectedMeal(updatedSelectedMeals)
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
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          height: '100%',
        }}
      >
        <Typography variant="h4" component="h1" gutterBottom align="center">
          {t('shopping-bag.title')}
        </Typography>

        {flattenSelectedMeals.length === 0 ? (
          <Typography variant="body1" align="center">
            {t('shopping-bag.empty')}
          </Typography>
        ) : (
          <Box
            sx={{
              display: 'grid',
              gridTemplateColumns: 'repeat(2, 1fr)',
              gap: 2,
              maxHeight: 'calc(100% - 100px)',
              overflow: 'auto',
              paddingBottom: '20px',
            }}
          >
            {flattenSelectedMeals.map((mealId: string, index: number) => {
              const meal = flattenMenu.find((meal) => meal._id === mealId)
              if (!meal) return null

              return (
                <MealCard
                  key={meal._id + index}
                  id={meal._id}
                  img={meal.image}
                  name={meal.shortName}
                  price={meal.price}
                  onDelete={() => deleteItem(meal._id)}
                />
              )
            })}
          </Box>
        )}
        <CommandCard
          items={spliceSelectedMeal
            .map((mealId: string) => {
              const meal = flattenMenu.find((meal) => meal._id === mealId)

              if (!meal) return null

              return {
                name: meal.shortName,
                img: meal.image,
              }
            })
            .filter((meal) => meal !== null)
            .map((meal) => meal as { name: string; img: string })}
          price={flattenSelectedMeals
            .map((mealId: string) => {
              const meal = flattenMenu.find((meal) => meal._id === mealId)
              return meal?.price || 0
            })
            .reduce((acc, price) => acc + price, 0)}
          primaryBtnProps={{
            label: 'Valider la commande',
            onClick: () => {
              validateCommand(selectedMeals, menu, tableNumber)
              navigate({
                to: routes.root.path,
              })
            },
          }}
          secondaryBtnProps={{
            label: 'Retour',
            onClick: () => {
              navigate({
                to: routes.command.path,
              })
            },
          }}
        />
      </Box>
    </Box>
  )
}

export default Panier