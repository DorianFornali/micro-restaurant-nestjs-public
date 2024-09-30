import { FC, useEffect, useState } from 'react';
import Divider from '@mui/material/Divider';
import { Box, List, ListItem, ListItemText } from '@mui/material';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import APP from '../../config/api';

interface StateBoardPerTable {
  table: string;
  timeRemaining: string; // Assuming time remaining is a string
}

interface StateBoard {
  preparationStarted: StateBoardPerTable[] | undefined;
  readyToBeServed: StateBoardPerTable[] | undefined;
}

// IN CONSTRUCTION, WAITING FOR THE PEOPLENODE TO BE IMPLEMENTED IN ORDER TO HAVE A RIGHT 
// REQUEST ON OUR BACKEND FROM THE FRONTEND 
const StateBoardPage: FC = () => {
  const { t } = useTranslation();
  const [stateBoard, setStateBoard] = useState<StateBoard | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Fetching state board data from the backend
  useEffect(() => {
    const fetchStateBoard = async () => {
      try {
        setLoading(true);
        const response = await axios.get(`${APP.API_STATE_BOARD}`);
        console.log(response.data);
        setStateBoard(response.data);
      } catch (err) {
        setError(t('error.fetchingData'));
      } finally {
        setLoading(false);
      }
    };

    fetchStateBoard();
  }, [t]);

  if (loading) {
    return <div>{t('loading')}</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  if (!stateBoard) {
    return <div>{t('noDataAvailable')}</div>;
  }

  return (
    <div>
      <h1>{t('state-board.starter')}</h1>
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
        {/* Preparation Started Section */}
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
          <h2>{t('state-board.preparationStarted')}</h2>
          <List>
            {stateBoard.preparationStarted && stateBoard.preparationStarted.length > 0 ? (
              stateBoard.preparationStarted.map((table) => (
                <ListItem key={table.table}>
                  <ListItemText
                    primary={`${t('table')} ${table.table}`}
                    secondary={t('timeRemaining', { time: table.timeRemaining })}
                  />
                </ListItem>
              ))
            ) : (
              <ListItem>
                <ListItemText primary={t('noTablesInPreparation')} />
              </ListItem>
            )}
          </List>
        </Box>

        <Divider orientation="vertical" flexItem />

        {/* Ready to be Served Section */}
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
          <h2>{t('state-board.readyToBeServed')}</h2>
          <List>
            {stateBoard.readyToBeServed && stateBoard.readyToBeServed.length > 0 ? (
              stateBoard.readyToBeServed.map((table) => (
                <ListItem key={table.table}>
                  <ListItemText
                    primary={`${t('table')} ${table.table}`}
                    secondary={t('timeRemaining', { time: table.timeRemaining })}
                  />
                </ListItem>
              ))
            ) : (
              <ListItem>
                <ListItemText primary={t('noTablesReady')} />
              </ListItem>
            )}
          </List>
        </Box>
      </Box>
    </div>
  );
};

export default StateBoardPage;
