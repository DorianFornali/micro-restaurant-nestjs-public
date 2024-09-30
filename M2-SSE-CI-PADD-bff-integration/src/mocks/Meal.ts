import { MealCardProps } from '../components/MealCard'

export const getCurrentStep = (step: number) => {
  return Object.keys(mealSteps)[step]
}

export const findStepBasedOnMealId = (mealId: string) => {
  for (const [key, value] of Object.entries(mealSteps)) {
    if (value.find((meal) => meal.id === mealId)) {
      return key
    }
  }
  return ''
}

export const mealCategories = [
  'boisson',
  'entrée',
  'plat',
  'dessert',
  'supplément',
]

export const mealSteps: { [key: string]: MealCardProps[] } = {
  boisson: [
    {
      id: '1',
      img: '/img/meal.avif',
      name: 'Coca-Cola',
      onSelect: () => {},
      price: 2,
    },
    {
      id: '2',
      img: '/img/meal.avif',
      name: 'Fanta',
      onSelect: () => {},
      price: 2,
    },
    {
      id: '3',
      img: '/img/meal.avif',
      name: 'Sprite',
      onSelect: () => {},
      price: 2,
    },
  ],
  entrée: [
    {
      id: '4',
      img: '/img/meal.avif',
      name: 'Salade',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '5',
      img: '/img/meal.avif',
      name: 'Soupe',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '6',
      img: '/img/meal.avif',
      name: 'Charcuterie',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '7',
      img: '/img/meal.avif',
      name: 'Salade',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '8',
      img: '/img/meal.avif',
      name: 'Soupe',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '9',
      img: '/img/meal.avif',
      name: 'Charcuterie',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '10',
      img: '/img/meal.avif',
      name: 'Salade',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '11',
      img: '/img/meal.avif',
      name: 'Soupe',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '12',
      img: '/img/meal.avif',
      name: 'Charcuterie',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '13',
      img: '/img/meal.avif',
      name: 'Salade',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '14',
      img: '/img/meal.avif',
      name: 'Soupe',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '15',
      img: '/img/meal.avif',
      name: 'Charcuterie',
      onSelect: () => {},
      price: 5,
    },
  ],
  plat: [
    {
      id: '16',
      img: '/img/meal.avif',
      name: 'Steak',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '17',
      img: '/img/meal.avif',
      name: 'Pâtes',
      onSelect: () => {},
      price: 5,
    },
    {
      id: '18',
      img: '/img/meal.avif',
      name: 'Riz',
      onSelect: () => {},
      price: 5,
    },
  ],
  dessert: [
    {
      id: '19',
      img: '/img/meal.avif',
      name: 'Glace',
      onSelect: () => {},
      price: 3,
    },
    {
      id: '20',
      img: '/img/meal.avif',
      name: 'Tarte',
      onSelect: () => {},
      price: 3,
    },
    {
      id: '21',
      img: '/img/meal.avif',
      name: 'Fruit',
      onSelect: () => {},
      price: 3,
    },
  ],
  supplément: [
    {
      id: '22',
      img: '/img/meal.avif',
      name: 'Fromage',
      onSelect: () => {},
      price: 1,
    },
    {
      id: '23',
      img: '/img/meal.avif',
      name: 'Pain',
      onSelect: () => {},
      price: 1,
    },
    {
      id: '24',
      img: '/img/meal.avif',
      name: 'Beurre',
      onSelect: () => {},
      price: 1,
    },
  ],
}

type Meal = {
  id: string
  name: string
  img: string
  price: number
}

export const initialCartItems: Meal[] = [
  {
    id: '12',
    name: 'Plat n°12',
    img: 'https://via.placeholder.com/400',
    price: 30,
  },
  {
    id: '13',
    name: 'Plat n°13',
    img: 'https://via.placeholder.com/400',
    price: 25,
  },
]
