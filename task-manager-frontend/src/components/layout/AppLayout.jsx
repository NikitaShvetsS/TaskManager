import React, { useContext, useEffect, useState } from "react";
import AppHeader from "./AppHeader";
import AppSider from "./AppSider";
import AppContent from "./AppContent";
import { Layout, Modal } from "antd";
import { AuthContext } from "../../context/AuthContext";
import SignUpForm from "../SighUpForm";

const layoutStyle = {
  borderRadius: 8,
  overflow: 'hidden',
  width: '100vw',
  maxWidth: '100%',
  height: '100vh'
};

export default function AppLayout() {

  const {token} = useContext(AuthContext)
  const [isLoginModalVisible, setIsLoginModalVisible] = useState(false)

  useEffect(() => {
    if(!token){
      setIsLoginModalVisible(true)
    }else {
      setIsLoginModalVisible(false)
    }
  }, [token])
  
    return (
      <Layout style={layoutStyle}>
      <AppHeader/>
      <Layout>
        <AppSider />
        <AppContent />
      </Layout>

      <Modal 
      title="Sign Up"
      open={isLoginModalVisible}
      footer={null}
      closable={false}
      centered
      width={400}
      maskClosable={false}
      styles={{mask: {
      backdropFilter: 'blur(5px)',
    }}}
      >
        <SignUpForm/>
      
      </Modal>
    </Layout>
    )
}