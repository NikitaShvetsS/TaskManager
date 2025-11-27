import React from "react";
import { Layout} from "antd";
import TasksPage from "../pages/TasksPage";

const contentStyle = {
  textAlign: 'center',
  minHeight: 'calc(100vh - 60px)',
  lineHeight: '120px',
  color: '#fff',
  backgroundColor: '#001519',
  padding: '1rem'
};

export default function AppContent(){
    return(
        <Layout.Content style={contentStyle}>
         <TasksPage />
        </Layout.Content>
    )
}