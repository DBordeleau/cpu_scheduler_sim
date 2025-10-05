import React, { useState } from 'react';
import '../style/Tooltip.css';

const Tooltip = ({ children, content }) => {
    const [isVisible, setIsVisible] = useState(false);

    return (
        <span
            className="tooltip-wrapper"
            onMouseEnter={() => setIsVisible(true)}
            onMouseLeave={() => setIsVisible(false)}
            onClick={() => setIsVisible(!isVisible)}
        >
            {children}
            {isVisible && (
                <span className="tooltip-content">
                    {content}
                </span>
            )}
        </span>
    );
};

export default Tooltip;