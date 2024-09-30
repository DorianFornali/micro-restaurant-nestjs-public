const APP = {
  API_URL: import.meta.env.VITE_API_URL,
  API_GET_MENU: import.meta.env.VITE_API_URL + '/menus',
  API_POST_NEW_ORDER: import.meta.env.VITE_API_URL + '/newOrder',
  API_SUPPLEMENTS: import.meta.env.VITE_API_URL + '/supplements',


  // API_URL_MENU: import.meta.env.VITE_API_URL + '/menu',
  // API_URL_DINING: import.meta.env.VITE_API_URL + '/dining',
  // API_URL_KITCHEN: import.meta.env.VITE_API_URL + '/kitchen',
};

export default APP
