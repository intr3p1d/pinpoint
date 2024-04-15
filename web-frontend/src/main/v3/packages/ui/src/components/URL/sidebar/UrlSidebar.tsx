import { AgentSearchList } from '../../Agent';
import { useUrlStatSearchParameters } from '@pinpoint-fe/hooks';
import {
  convertParamsToQueryString,
  getFormattedDateRange,
  getUrlStatPath,
} from '@pinpoint-fe/utils';
import { useNavigate } from 'react-router-dom';

export const UrlSidebar = () => {
  const navigate = useNavigate();
  const { application, dateRange, agentId } = useUrlStatSearchParameters();
  return (
    <div className="w-60 min-w-[15rem] border-r-1 h-full">
      <AgentSearchList
        selectedAgentId={agentId}
        onClickAgent={(agent) => {
          navigate(
            `${getUrlStatPath(application)}?${convertParamsToQueryString({
              ...getFormattedDateRange(dateRange),
              agentId: agentId === agent?.agentId ? '' : agent?.agentId,
            })}`,
          );
        }}
      />
    </div>
  );
};
