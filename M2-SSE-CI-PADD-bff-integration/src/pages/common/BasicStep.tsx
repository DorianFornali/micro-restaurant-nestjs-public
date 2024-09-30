import { FC } from 'react'
import {
  Box,
  Button,
  ButtonProps,
  TextField,
  TextFieldProps,
  Typography,
} from '@mui/material'
import ArrowRight from '@mui/icons-material/ArrowRight'

type BasicStepProps = {
  title: string
  inputProps: TextFieldProps
  submitBtnProps: ButtonProps & { label: string }
}

const BasicStep: FC<BasicStepProps> = (props) => {
  const { title, inputProps, submitBtnProps } = props

  return (
    <Box
      component="div"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-evenly',
        height: '100%',
        padding: 2,
      }}
    >
      <Typography variant="h3" component="h1">
        {title}
      </Typography>
      <TextField required size="medium" type="number" {...inputProps} />
      <Button
        variant="contained"
        color="primary"
        size="large"
        endIcon={<ArrowRight />}
        {...submitBtnProps}
      >
        {submitBtnProps.label}
      </Button>
    </Box>
  )
}

export default BasicStep
