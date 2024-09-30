import { useNavigate } from "@tanstack/react-router";
import APP from "../config/api";
import { routes } from "../router/definitions";
import { CommandSlice } from "../store/slices/command";
import { Menu, MenuItem, MenuItemCategoryAll } from "../types/Menu";

type UseCommandReturn = {
    validateCommand: (selectedMeals: CommandSlice["selectedMeal"], menu: Menu, tableNumber: number) => void;
}

const useCommand = (): UseCommandReturn => {
    const navigate = useNavigate();

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
      
      
    
      const validateCommand = (selectedMeals: CommandSlice["selectedMeal"], menu: Menu, tableNumber: number) => {
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


    return { validateCommand }

}

export default useCommand;