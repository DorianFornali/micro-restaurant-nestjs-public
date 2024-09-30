import { useTranslation } from 'react-i18next'
import { FC, useState } from 'react'
import Divider from '@mui/material/Divider'
import { Box, List, ListItem, ListItemText } from '@mui/material'

const StateBoardPage: FC = () => {
  const { t } = useTranslation()
  const [title] = useState(t('state-board.starter'))

  return (
    <div>
      <h1>{title}</h1>
      <Divider />
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'row',
          gap: 2,
          flexGrow: 1,
          justifyContent: 'center',
          alignItems: 'center',
          paddingX: 1,
        }}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
            flexGrow: 1,
            justifyContent: 'center',
            alignItems: 'center',
            paddingX: 1,
          }}
        >
          <List>
            <ListItem>
              <ListItemText primary="Table 12" />
            </ListItem>
            <ListItem>
              <ListItemText primary="Table 8" />
            </ListItem>
          </List>
        </Box>
        <Divider orientation="vertical" flexItem />
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            gap: 2,
            flexGrow: 1,
            justifyContent: 'center',
            alignItems: 'center',
            paddingX: 1,
          }}
        >
          <List>
            <ListItem>
              <ListItemText primary="Table 12" />
            </ListItem>
            <ListItem>
              <ListItemText primary="Table 8" />
            </ListItem>
          </List>
        </Box>
      </Box>
    </div>
  )
}

export default StateBoardPage
