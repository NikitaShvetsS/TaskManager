import React, { createContext, useEffect, useState } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);

  
  useEffect(() => {
  const savedUser = sessionStorage.getItem("user");
  const savedToken = sessionStorage.getItem("token");

  let parsedUser = null;
  try {
    parsedUser = savedUser ? JSON.parse(savedUser) : null;
  } catch (e) {
    console.warn("Invalid user data in sessionStorage, clearing it...");
    sessionStorage.removeItem("user");
  }

  if (parsedUser && savedToken) {
    setUser(parsedUser);
    setToken(savedToken);
  }
}, []);

  
  const login = (userData, jwtToken) => {
    setUser(userData);
    setToken(jwtToken);
    sessionStorage.setItem("user", JSON.stringify(userData)); // 🟢 исправлено
    sessionStorage.setItem("token", jwtToken);
  };

  
  const logout = () => {
    setUser(null);
    setToken(null);
    sessionStorage.removeItem("user");
    sessionStorage.removeItem("token");
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};


export default AuthProvider;