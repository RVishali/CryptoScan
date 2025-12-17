import React, { useState } from "react";
import Notification from "./Notification";

const ForgotPassword = ({ onBack }) => {
  const [email, setEmail] = useState("");
  const [notif, setNotif] = useState({ message: "", type: "info" });

  const handleSubmit = async (e) => {
    e.preventDefault();
    console.log("FORGOT PASSWORD SUBMIT CLICKED", email);

    try {
      setNotif({ message: "Sending reset link...", type: "info" });

      const res = await fetch("http://localhost:8080/api/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email })
      });

      setNotif({
        message:
          "If an account exists for this email, a reset link has been sent.",
        type: "success"
      });

    } catch (err) {
      console.error(err);
      setNotif({ message: "Failed to send reset link.", type: "danger" });
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <Notification
        message={notif.message}
        type={notif.type}
        onClose={() => setNotif({ message: "", type: "info" })}
      />

      <div className="mb-3">
        <label>Email</label>
        <input
          type="email"
          className="form-control cyberpunk-input"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
      </div>

      <button type="submit" className="btn btn-cyber w-100">
        Send Reset Link
      </button>

      <button
        type="button"
        className="btn btn-link w-100 mt-2"
        onClick={onBack}
      >
        Back to Login
      </button>
    </form>
  );
};

export default ForgotPassword;
