import React from "react";
import { Layout, Modal, Drawer, Button } from "antd";
import { useState, useContext } from "react";
import SignUpForm from "../SighUpForm";
import { AuthContext } from "../../context/AuthContext";

const headerStyle = {
  width: '100%',
  textAlign: 'center',
  height: 60,
  padding: '1rem',
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  color: 'white'
};

export default function AppHeader(){
   const [modal, setModal] = useState(false)
   const [drawer, setDrawer] = useState(false)
   const {user, logout} = useContext(AuthContext)


   const handleButtonClick = () => {
    if (user) {
      logout();
    } else {
      setDrawer(true);
    }
  }

    return(
        <Layout.Header style={headerStyle}>
      <h2 style={{ color: "#fff" }}>Dashboard</h2>

      <Button type="primary" onClick={handleButtonClick}>
        {user ? user.username : "Sign Up"}
      </Button>

      <Modal
        closable={{ "aria-label": "Custom Close Button" }}
        open={modal}
        onCancel={() => setModal(false)}
        footer={null}
      />

      <Drawer
        width={400}
        title="Sign Up"
        closable={{ "aria-label": "Close Button" }}
        onClose={() => setDrawer(false)}
        open={drawer}
        destroyOnClose
      >
        <SignUpForm onClose={() => setDrawer(false)} />
      </Drawer>
    </Layout.Header>
    )
}