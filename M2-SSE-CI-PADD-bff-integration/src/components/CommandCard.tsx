import { Box, Button, ButtonProps, Typography } from '@mui/material'
import { FC } from 'react'

type CommandCardProps = {
  items: {
    name: string
    img: string
  }[]
  price: number
  secondaryBtnProps: ButtonProps & { label: string }
  primaryBtnProps: ButtonProps & { label: string }
}

const CommandCard: FC<CommandCardProps> = (props) => {
  const { items, price, secondaryBtnProps, primaryBtnProps } = props

  return (
    <Box
      component="div"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: '#cccccc',
        padding: 2,
      }}
    >
      <Box
        component="div"
        sx={{ display: 'flex', flexDirection: 'row', marginBottom: '8px' }}
      >
        {items &&
          items.length > 0 &&
          items.slice(0, 4).map((item, index) => (
            <Box
              key={item.name + index}
              component="div"
              sx={{
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                marginRight: '8px',
              }}
            >
              <Box
                component="img"
                src={item.img}
                alt={item.name}
                sx={{
                  width: '32px',
                  height: '32px',
                  objectFit: 'cover',
                  borderRadius: '50%',
                }}
              />
              <Typography variant="h6" component="h2">
                {item.name}
              </Typography>
            </Box>
          ))}
        {items && items.length > 4 && (
          <Typography variant="h4" component="h2">
            ...
          </Typography>
        )}
        <Typography
          variant="h4"
          component="h2"
          sx={{
            marginLeft: 'auto',
          }}
        >
          {price} €
        </Typography>
      </Box>
      <Box
        component="div"
        sx={{
          display: 'flex',
          flexDirection: 'row',
          width: '100%',
          justifyContent: 'space-evenly',
        }}
      >
        <Button variant="contained" color="secondary" {...secondaryBtnProps}>
          {secondaryBtnProps.label}
        </Button>
        <Button variant="contained" color="primary" {...primaryBtnProps}>
          {primaryBtnProps.label}
        </Button>
      </Box>
    </Box>
  )
}

export default CommandCard
