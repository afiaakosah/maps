import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom'
import {App, TEXT_header_div} from '../App';


beforeEach(() => {
    render(<App />);
});

/**
 * Test that checks whether the header is in the app
 */
test('renders map', async () => {
    const map = await screen.getByText(TEXT_header_div);
    expect(map).toBeInTheDocument();
  });
