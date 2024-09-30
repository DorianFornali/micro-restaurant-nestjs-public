const APP = {
  API_URL: import.meta.env.VITE_API_URL,
  API_GET_MENU: import.meta.env.VITE_API_URL + '/bff/menus',
  API_POST_NEW_ORDER: import.meta.env.VITE_API_URL + '/bff/newOrder',
  API_SUPPLEMENTS: import.meta.env.VITE_API_URL + '/bff/supplements',
  API_STATE_BOARD: import.meta.env.VITE_API_URL + '/bff/preparations'

  // API_URL_MENU: import.meta.env.VITE_API_URL + '/menu',
  // API_URL_DINING: import.meta.env.VITE_API_URL + '/dining',
  // API_URL_KITCHEN: import.meta.env.VITE_API_URL + '/kitchen',
};

export default APP
