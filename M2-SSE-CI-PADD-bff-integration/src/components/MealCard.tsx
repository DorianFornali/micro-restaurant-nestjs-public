import { FC, useState } from 'react'
import { Box, Button, Modal, TextField, Typography } from '@mui/material'
import DeleteIcon from '@mui/icons-material/Delete'
import { useTranslation } from 'react-i18next'

export type MealCardProps = {
  id: string
  img: string
  name: string
  onClick?: () => void
  onSelect?: (numberOfItem?: number) => void
  openModalOnSelect?: boolean
  isSelected?: boolean
  onDelete?: () => void
  price: number
}

const MealCard: FC<MealCardProps> = (props) => {
  const {
    img,
    name,
    onClick,
    onSelect,
    openModalOnSelect,
    isSelected,
    onDelete,
    price,
  } = props
  const { t } = useTranslation()

  const [open, setOpen] = useState(false)
  const [numberOfPeople, setNumberOfPeople] = useState(1)

  return (
    <Box
      component="div"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'flex-end',
        padding: 2,
        position: 'relative',
        width: '100%',
        height: '150px',
        borderRadius: 2,
        boxShadow: isSelected ? '0px 0px 10px 0px #000000' : 'none',
      }}
      onClick={onClick}
    >
      <Box
        component="img"
        src={img}
        alt={name}
        sx={{
          width: '100%',
          height: '100%',
          objectFit: 'cover',
          position: 'absolute',
          top: 0,
          left: 0,
          borderRadius: 2,
        }}
      />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          width: 'fit-content',
          backgroundColor: '#FFF',
          padding: 1,
          borderRadius: 2,
          zIndex: 2,
        }}
      >
        <Typography variant="h4" component="h2">
          {name}
        </Typography>
        <Typography variant="body1" component="p">
          {price} €
        </Typography>
      </Box>

      {onSelect && (
        <Box
          sx={{
            position: 'absolute',
            top: 12,
            left: 12,
            zIndex: 2,
            backgroundColor: isSelected ? '#42ff3f' : '#b8b8b8',
            width: 32,
            height: 32,
            borderRadius: '50%',
          }}
          onClick={(e) => {
            e.stopPropagation()
            if (openModalOnSelect) setOpen(true)
            else onSelect()
          }}
        ></Box>
      )}
      {onDelete && (
        <DeleteIcon
          sx={{
            color: 'red',
            cursor: 'pointer',
            position: 'absolute',
            top: 12,
            right: 12,
            width: 32,
            height: 32,
          }}
          onClick={(e) => {
            e.stopPropagation()
            onDelete()
          }}
        />
      )}

      <Modal open={open} onClose={() => setOpen(false)}>
        <Box
          sx={{
            position: 'absolute' as const,
            top: '50%',
            left: '50%',
            transform: 'translate(-50%, -50%)',
            width: 400,
            bgcolor: 'background.paper',
            border: '2px solid #000',
            boxShadow: 24,
            p: 4,
          }}
          onClick={(e) => e.stopPropagation()}
        >
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
            <Typography variant="h6" component="h2">
              {t('component.mealCard.modal.title')}
            </Typography>
            <TextField
              id="number"
              label={t('component.mealCard.modal.label')}
              type="number"
              value={numberOfPeople}
              onChange={(e) => setNumberOfPeople(parseInt(e.target.value))}
            />
            <Button
              variant="contained"
              color="primary"
              onClick={() => {
                onSelect?.(numberOfPeople)
                setOpen(false)
              }}
            >
              {t('component.mealCard.modal.submit')}
            </Button>
          </Box>
        </Box>
      </Modal>
    </Box>
  )
}

export default MealCard
