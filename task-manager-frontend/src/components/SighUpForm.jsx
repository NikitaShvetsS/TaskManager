import React, { useState, useContext } from "react";
import { Form, Input, Button, Checkbox, Flex, message } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { AuthContext } from "../context/AuthContext";
import { loginUser } from "../api/auth";
import RegistrationForm from "./RegistrationForm";

export default function SignUpForm() {
  const { login } = useContext(AuthContext);

  const [isRegistering, setIsRegistering] = useState(false);
  const [isRegistered, setIsRegistered] = useState(false);
  const [loginSuccess, setLoginSuccess] = useState(false);

  const onLoginFinish = async (values) => {
  try {
    const response = await loginUser(values.email, values.password);

    if (response.status === 200) {
      const { token } = response.data;
      if (!token) throw new Error("Token not found in response");

      const userData = { email: values.email };

      login(userData, token);

      message.success("Login successful!");
      setLoginSuccess(true);
      console.log("🟢 Server response:", response.data);
    } else {
      message.error("Unexpected server response.");
    }
  } catch (error) {
    console.error("Login error:", error);
    message.error("Invalid username or password!");
  }
};

  const onLoginFailed = (errorInfo) => {
    console.log("Login failed:", errorInfo);
    message.error("Please check your input.");
  };

  const handleRegisterSuccess = () => {
    setIsRegistered(true);
    setIsRegistering(false);
    message.success("Registration successful! Please log in.");
    setTimeout(() => setIsRegistered(false), 3000);
  };


  if (loginSuccess) {
    return (
      <div style={{ textAlign: "center", padding: "40px 0" }}>
        <h2>✅ Login Successful!</h2>
      </div>
    );
  }

  if (isRegistering) {
    return <RegistrationForm onSuccess={handleRegisterSuccess} />;
  }

  if (isRegistered) {
    return (
      <div style={{ textAlign: "center" }}>
        <h3>✅ Registration successful!</h3>
        <p>You can now log in below.</p>
        <Button type="primary" onClick={() => setIsRegistered(false)}>
          Back to Login
        </Button>
      </div>
    );
  }


  return (
    <Form
      name="login"
      labelCol={{ span: 8 }}
      wrapperCol={{ span: 16 }}
      style={{ maxWidth: 360 }}
      initialValues={{ remember: true }}
      onFinish={onLoginFinish}
      onFinishFailed={onLoginFailed}
    >
      <Form.Item
        name="email"
        rules={[{ required: true, message: "Please input your email!" }]}
      >
        <Input prefix={<UserOutlined />} placeholder="Email" />
      </Form.Item>

      <Form.Item
        name="password"
        rules={[{ required: true, message: "Please input your Password!" }]}
      >
        <Input.Password prefix={<LockOutlined />} placeholder="Password" />
      </Form.Item>

      <Form.Item>
        <Flex justify="space-between" align="center">
          <Form.Item name="remember" valuePropName="checked" noStyle>
            <Checkbox>Remember me</Checkbox>
          </Form.Item>
          <a href="#">Forgot password</a>
        </Flex>
      </Form.Item>

      <Form.Item>
        <Button block type="primary" htmlType="submit">
          Log in
        </Button>
        <div style={{ marginTop: 10, textAlign: "center" }}>
          or{" "}
          <a
            href="#"
            onClick={(e) => {
              e.preventDefault();
              setIsRegistering(true);
            }}
          >
            Register now!
          </a>
        </div>
      </Form.Item>
    </Form>
  );
}