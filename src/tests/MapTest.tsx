import React from "react";
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/react';
import { MapData, MapElements } from "../Map";
import { mapData } from "../overlays";

/**
 * Rendering the main function before every function
 */
 beforeEach(() =>{
    render(<MapElements/>);
})



