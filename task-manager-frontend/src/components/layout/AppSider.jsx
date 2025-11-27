import React, { useEffect, useState } from 'react';
import { Layout, Menu, Switch, Modal, Descriptions, Spin, message } from 'antd';
import { MailOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import axiosClient from '../../api/axiosClient';
import { useContext } from 'react';
import { AuthContext } from '../../context/AuthContext';
import { TaskContext } from '../../context/TaskContext';

const siderStyle = {
  padding: '1rem',
  textAlign: 'center',
  color: '#e3e3e3ff',
};

export default function AppSider() {
  const [theme, setTheme] = useState('dark')
  const [current, setCurrent] = useState(null)
  const {tasks} = useContext(TaskContext)
  const [selectedTask, setSelectedTask] = useState(null)
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [loading, setLoading] = useState(false)

  const token = (() => {
    try {
      return useContext(AuthContext)
    } catch (e) {
      console.error('Error reading token from localStorage', e);
      return null;
    }
  })();

  // useEffect(() => {
  
  //   if (!token) {
  //     setLoading(false);
  //     console.log('AppSider: no token — skipping fetchTasks');
  //     return;
  //   }
  //   if (axiosClient && axiosClient.defaults) {
  //     axiosClient.defaults.headers.common['Authorization'] = `Bearer ${token}`;
  //   }

  //   let mounted = true;
  //   setLoading(true);

  //   (async () => {
  //     try {
  //       const resp = await axiosClient.get('task/all');
  //       if (!mounted) return;
  //       const data = Array.isArray(resp?.data) ? resp.data : [];
  //       setTasks(data);
  //     } catch (err) {
  //       console.error('AppSider: error fetching tasks', err);
  //       message.error('Failed to load tasks (AppSider)');
  //     } finally {
  //       if (mounted) setLoading(false);
  //     }
  //   })();

  //   return () => {
  //     mounted = false;
  //   };
  // }, [token]);

  const changeTheme = (value) => setTheme(value ? 'dark' : 'light');

  const handleMenuClick = (e) => {
    if (!e?.key) return;
    if (e.key === 'empty') return;
    const found = tasks.find((t) => String(t.id) === String(e.key));
    if (found) {
      setSelectedTask(found);
      setIsModalOpen(true);
      setCurrent(e.key);
    } else {
      console.warn('AppSider: clicked key not found in tasks', e.key);
    }
  };

  const tasksChildren = tasks.length
  ? tasks.map((task) => ({
      key: String(task.id),
      label: task.title || `Task #${task.id}`,
    }))
  : [{ key: 'empty', label: 'No tasks found', disabled: true }];
  
  return (
    <Layout.Sider width="25%" style={siderStyle}>
      <Switch
        checked={theme === 'dark'}
        onChange={changeTheme}
        checkedChildren="Dark"
        unCheckedChildren="Light"
      />
      <br />
      <br />

      {loading ? (
        <div style={{ paddingTop: 20 }}>
          <Spin tip="Loading tasks..." />
        </div>
      ) : token ? (
        <Menu
          theme={theme}
          onClick={handleMenuClick}
          style={{ width: 256 }}
          defaultOpenKeys={['sub1']}
          selectedKeys={[current].filter(Boolean)}
          mode="inline"
          items={[
            {
              key: 'sub1',
              label: 'Tasks',
              icon: <MailOutlined />,
              children: tasksChildren,
            },
          ]}
        />
      ) : (
        <div style={{ padding: 12, color: '#aaa' }}>Login to see your tasks</div>
      )}

      <Modal
        title={selectedTask ? selectedTask.title : 'Task Info'}
        open={isModalOpen}
        onCancel={() => setIsModalOpen(false)}
        footer={null}
        destroyOnClose
      >
        {selectedTask ? (
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="Description">
              {selectedTask.description || '—'}
            </Descriptions.Item>
            <Descriptions.Item label="Priority">
              {selectedTask.priority || '—'}
            </Descriptions.Item>
            <Descriptions.Item label="Status">
              {selectedTask.status || '—'}
            </Descriptions.Item>
            <Descriptions.Item label="Deadline">
              {selectedTask.deadline
                ? dayjs(selectedTask.deadline).format('YYYY-MM-DD')
                : '—'}
            </Descriptions.Item>
          </Descriptions>
        ) : (
          <div>No task data</div>
        )}
      </Modal>
    </Layout.Sider>
  );
}