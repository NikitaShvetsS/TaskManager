import { useState, useContext, useEffect } from "react";
import { Table, Modal, Button, Form, Input, Select, DatePicker, message, Badge, Switch } from "antd";
import dayjs from "dayjs";
import axiosClient from "../../api/axiosClient";
import { TaskContext } from "../../context/TaskContext";
import { AuthContext } from "../../context/AuthContext";

export default function TasksPage() {
  const { tasks, setTasks } = useContext(TaskContext);
  const { token } = useContext(AuthContext);
  const [theme, setTheme] = useState("dark");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isWarningModalOpen, setIsWarningModalOpen] = useState(false);
  const [form] = Form.useForm();
  const [updatingTask, setUpdatingTask] = useState(null);

  useEffect(() => {
    if (!token) return;

    axiosClient.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    fetchTasks();
  }, [token]);

  const fetchTasks = async () => {
    try {
      const resp = await axiosClient.get("task/all");
      setTasks(Array.isArray(resp.data) ? resp.data : []);
    } catch (err) {
      console.error("Error fetching tasks:", err);
      message.error("Failed to fetch tasks");
    }
  };

  const handleAddTask = () => {
    token ? setIsModalOpen(true) : setIsWarningModalOpen(true);
  };

  const handleCancel = () => {
    form.resetFields();
    setIsModalOpen(false);
    setUpdatingTask(null);
  };

  const handleCreateTask = async (values) => {
  const newTask = {
    title: values.title,
    description: values.description,
    priority: values.priority?.toUpperCase() || 'LOW',
    status: values.status || 'TODO',
    deadline: values.deadline ? values.deadline.format('YYYY-MM-DD') : null,
  };

  try {
    let createdTask;
    if (updatingTask) {
      const res = await axiosClient.put(`task/update/${updatingTask.id}`, newTask);
      createdTask = res.data; 
      setTasks((prev) =>
        prev.map((t) => (t.id === updatingTask.id ? createdTask : t))
      );
      message.success('Task updated successfully!');
    } else {
      const res = await axiosClient.post('task/add', newTask);
      createdTask = res.data; 
      setTasks((prev) => [...prev, createdTask]);
      message.success('Task added successfully!');
    }

    form.resetFields();
    setIsModalOpen(false);
    setUpdatingTask(null);
    fetchTasks()
  } catch (error) {
    console.error('Error saving task:', error);
    message.error('Failed to save task');
  }
};

  const handleDeleteTask = async (taskId) => {
    try {
      await axiosClient.delete(`task/${taskId}`);
      setTasks(prev => prev.filter(t => t.id !== taskId));
      message.success("Task deleted successfully!");
    } catch (err) {
      console.error("Error deleting task:", err);
      message.error("Failed to delete task");
    }
  };

  const handleUpdateTask = (task) => {
    setUpdatingTask(task);
    setIsModalOpen(true);
    form.setFieldsValue({
      title: task.title,
      description: task.description,
      priority: task.priority,
      status: task.status,
      deadline: task.deadline ? dayjs(task.deadline) : null,
    });
  };

  const columns = [
    { title: "ID", dataIndex: "id", key: "id" },
    { title: "Title", dataIndex: "title", key: "title" },
    { title: "Description", dataIndex: "description", key: "description" },
    { title: "Priority", dataIndex: "priority", key: "priority" },
    {
      title: "Status",
      key: "status",
      render: (_, record) => {
        const statusText = record?.status || "In Progress";
        return (
          <Badge
            status={statusText === "FINISHED" ? "success" : "processing"}
            text={statusText}
          />
        );
      },
    },
    { title: "Deadline", dataIndex: "deadline", key: "deadline" },
    {
      title: "Actions",
      key: "actions",
      render: (_, record) => (
        <>
          <Button type="link" onClick={() => handleUpdateTask(record)}>Edit</Button>
          <Button type="link" danger onClick={() => handleDeleteTask(record.id)}>Delete</Button>
        </>
      ),
    },
  ];

  return (
    <>
      {/* <div style={{ marginBottom: 16, display: "flex", justifyContent: "space-between" }}>
        <Switch
          checked={theme === "dark"}
          onChange={value => setTheme(value ? "dark" : "light")}
          checkedChildren="Dark"
          unCheckedChildren="Light"
        />
        <Button type="primary" onClick={handleAddTask}>Add Task</Button>
      </div> */}



      <Table
        rowKey="id"
        style={{tableLayout: 'fixed'}}
        columns={columns}
        dataSource={tasks}
        expandable={{ expandedRowRender: (record) => <p style={{ margin: 0 }}>{record.description}</p> }}
        pagination={{ pageSize: 5, showSizeChanger: false, hideOnSinglePage: true }}
      />

      <Modal
        title={updatingTask ? "Update Task" : "Create New Task"}
        open={isModalOpen}
        onCancel={handleCancel}
        onOk={() => form.submit()}
        destroyOnClose
      >
        <Form form={form} layout="vertical" onFinish={handleCreateTask}>
          <Form.Item name="title" label="Title" rules={[{ required: true, message: "Please enter a title!" }]}>
            <Input placeholder="Enter task title" />
          </Form.Item>
          <Form.Item name="description" label="Description">
            <Input.TextArea rows={3} placeholder="Describe the task" />
          </Form.Item>
          <Form.Item name="priority" label="Priority">
            <Select placeholder="Select priority">
              <Select.Option value="Low">Low</Select.Option>
              <Select.Option value="Medium">Medium</Select.Option>
              <Select.Option value="High">High</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="status" label="Status">
            <Select placeholder="Select status">
              <Select.Option value="TODO">TODO</Select.Option>
              <Select.Option value="IN_PROGRESS">IN_PROGRESS</Select.Option>
              <Select.Option value="FINISHED">FINISHED</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item name="deadline" label="Deadline">
            <DatePicker style={{ width: "100%" }} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Authentication Required"
        open={isWarningModalOpen}
        onCancel={() => setIsWarningModalOpen(false)}
        footer={[
          <Button key="ok" type="primary" onClick={() => setIsWarningModalOpen(false)}>OK</Button>
        ]}
      >
        <p>You must be logged in to add a new task.</p>
      </Modal>
    </>
  );
}