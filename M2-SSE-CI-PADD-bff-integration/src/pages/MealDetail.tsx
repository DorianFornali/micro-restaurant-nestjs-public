import { FC } from 'react'
import { Box, Button, Typography } from '@mui/material'
import { useNavigate, useParams } from '@tanstack/react-router'
import { routes } from '../router/definitions'
import { useSuspenseQuery } from '@tanstack/react-query'
import { mealDetailQuery } from '../router/postQueries'
import { MenuItem } from '../types/Menu'

const MealDetail: FC = () => {
  const { mealId } = useParams({
    strict: false,
  })

  const navigate = useNavigate() // Create a navigate function to handle back navigation

  const mealDetailQueryOptions = mealDetailQuery(mealId ?? '')
  const { data } = useSuspenseQuery(mealDetailQueryOptions)
  const mealDetail = data as MenuItem

  const handleGoBack = () => {
    // not correctly working (may be differ the url for the menu display )
    navigate({
      to: routes.command.path,
    }) // Navigate back to the previous page without adding a new entry to the browser history
  }

  return (
    <Box
      component="div"
      sx={{
        padding: '20px',
        maxWidth: '600px',
        margin: 'auto',
        border: 'none',
        borderRadius: 'none',
      }}
    >
      {/* Back button */}
      <button
        onClick={handleGoBack}
        style={{
          background: 'none',
          border: 'none',
          cursor: 'pointer',
          marginBottom: '10px',
        }}
      >
        <span style={{ fontSize: '24px', fontWeight: 'bold' }}>←</span>
      </button>

      <span
        style={{ fontSize: '24px', fontWeight: 'bold', fontStyle: 'italic' }}
      >
        {' '}
        {mealDetail.shortName}{' '}
      </span>
      <Box
        component="img"
        src={mealDetail.image}
        alt={mealDetail.shortName}
        sx={{
          width: '100%',
          height: 'auto',
          borderRadius: '8px',
          marginBottom: '10px',
        }}
      />

      <Box
        component="div"
        sx={{
          padding: '20px',
          maxWidth: '600px',
          margin: 'auto',
          border: 'none',
          borderRadius: 'none',
        }}
      >
        <Typography variant="h5" component="p">
          Description:
        </Typography>
        <br />
        <Typography variant="h5" component="p">
          {mealDetail.fullName}
        </Typography>
      </Box>

      <Box
        sx={{ textAlign: 'center', marginTop: '20px', marginBottom: '20px' }}
      >
        <Button
          variant="contained"
          color="primary"
          onClick={() => {}}
          sx={{ padding: '10px 20px' }}
        >
          Choisir ce plat
        </Button>
      </Box>
    </Box>
  )
}

export default MealDetail
