import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import MovieSelect from "./MovieSelect.jsx";
import MovieDetail from "./MovieDetial";
import Pay from "./Pay";
import Payend from "./Payend";

const DefaultRouter = () => {
  return (
    <Routes>
      <Route path="" element={<MovieSelect />}></Route>
      <Route path=":movieCode" element={<MovieDetail />}></Route>
      <Route path=":movieCode/pay" element={<Pay />}></Route>
      <Route path=":movieCode/pay/payend" element={<Payend />}></Route>
    </Routes>
  );
};
export default DefaultRouter;