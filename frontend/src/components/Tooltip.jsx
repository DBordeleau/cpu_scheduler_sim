import React, { useState, useEffect, useRef } from 'react';
import '../style/Tooltip.css';

const Tooltip = ({ children, content }) => {
    const [isVisible, setIsVisible] = useState(false);
    const wrapperRef = useRef(null);

    // Close tooltip when clicking outside
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (wrapperRef.current && !wrapperRef.current.contains(event.target)) {
                setIsVisible(false);
            }
        };

        if (isVisible) {
            document.addEventListener('mousedown', handleClickOutside);
            document.addEventListener('touchstart', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
            document.removeEventListener('touchstart', handleClickOutside);
        };
    }, [isVisible]);

    const handleToggle = (e) => {
        // Prevent the button click from triggering when opening tooltip on mobile
        if (window.innerWidth <= 768) {
            e.stopPropagation();
            setIsVisible(!isVisible);
        }
    };

    return (
        <span
            ref={wrapperRef}
            className="tooltip-wrapper"
            onMouseEnter={() => setIsVisible(true)}
            onMouseLeave={() => setIsVisible(false)}
            onClick={handleToggle}
        >
            {children}
            {isVisible && (
                <span className="tooltip-content">
                    {content}
                    <button
                        className="tooltip-close"
                        onClick={(e) => {
                            e.stopPropagation();
                            setIsVisible(false);
                        }}
                        aria-label="Close tooltip"
                    >
                        ✕
                    </button>
                </span>
            )}
        </span>
    );
};

export default Tooltip;