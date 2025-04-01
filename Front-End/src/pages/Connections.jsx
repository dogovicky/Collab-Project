import { useEffect, useState } from "react";
import ConnectionList from "../components/ConnectionList";
import Header from "../components/header";
import "./CssSheets/connections.css";

const Connections = () => {
  const [users, setUsers] = useState([]);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const response = await fetch("https://api.example.com/users"); // Replace with your API endpoint
        const data = await response.json();
        setUsers(data);
      } catch (error) {
        console.error("Error fetching users:", error);
      }
    };

    fetchUsers();
  }, []);

  return (
    <>
      <Header />
      <div className="p-4">
        <h1 className="text-2xl font-bold mb-4">Connections</h1>
        <ConnectionList users={users} />
      </div>
    </>
  );
};

export default Connections;
