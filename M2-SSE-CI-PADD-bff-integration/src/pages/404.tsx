import { useNavigate } from '@tanstack/react-router'
import { Button } from '@mui/material'
import { useTranslation } from 'react-i18next'

const NotFoundPage = () => {
  const navigate = useNavigate()
  const { t } = useTranslation()
  return (
    <div>
      <h1>{t('404.title')}</h1>
      <Button
        onClick={() =>
          navigate({
            to: '/',
          })
        }
      >
        {t('404.back')}
      </Button>
    </div>
  )
}

export default NotFoundPage
